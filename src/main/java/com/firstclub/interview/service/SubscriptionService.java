package com.firstclub.interview.service;

import com.firstclub.interview.dto.MembershipResponse;
import com.firstclub.interview.dto.SubscribeRequest;
import com.firstclub.interview.entity.*;
import com.firstclub.interview.enums.MembershipStatus;
import com.firstclub.interview.model.UserMetrics;
import com.firstclub.interview.repository.*;
import com.firstclub.interview.statemachine.SubscriptionStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final TierRepository tierRepository;
    private final MembershipHistoryRepository historyRepository;
    private final TierEvaluationService tierEvaluationService;

    @Transactional
    public MembershipResponse subscribe(SubscribeRequest req) {
        User user = userRepository.findByIdForUpdate(req.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.userId()));

        // Reject if user already has an active membership
        if (membershipRepository.findByUserIdAndStatus(req.userId(), MembershipStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException(
                "User already has an active membership. Cancel it before subscribing to a new plan.");
        }

        MembershipPlan plan = planRepository.findByPlanType(req.planType())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + req.planType()));
        Tier tier = tierRepository.findById(req.tierId())
                .orElseThrow(() -> new IllegalArgumentException("Tier not found: " + req.tierId()));

        if (!tierEvaluationService.qualifiesFor(tier, toMetrics(user))) {
            throw new IllegalStateException("User does not qualify for tier: " + tier.getName());
        }

        UserMembership membership = buildMembership(user, plan, tier);
        membershipRepository.save(membership);
        recordHistory(membership.getId(), "SUBSCRIBED", null, tier.getName(), null);
        return MembershipResponse.from(membership);
    }

    @Transactional
    public MembershipResponse upgradeTier(Long membershipId, Long newTierId) {
        return changeTier(membershipId, newTierId, MembershipStatus.UPGRADED);
    }

    @Transactional
    public MembershipResponse downgradeTier(Long membershipId, Long newTierId) {
        return changeTier(membershipId, newTierId, MembershipStatus.DOWNGRADED);
    }

    @Transactional
    public MembershipResponse cancel(Long membershipId) {
        UserMembership membership = getActiveMembership(membershipId);
        SubscriptionStateMachine.transition(membership.getStatus(), MembershipStatus.CANCELLED);
        membership.setStatus(MembershipStatus.CANCELLED);
        membershipRepository.save(membership);
        recordHistory(membershipId, "CANCELLED", membership.getTier().getName(), null, null);
        return MembershipResponse.from(membership);
    }

    public MembershipResponse getCurrentMembership(Long userId) {
        return membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.ACTIVE)
                .map(MembershipResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("No active membership for user: " + userId));
    }

    public Page<MembershipResponse> getMembershipHistory(Long userId, int page, int size) {
        return membershipRepository.findByUserId(userId, PageRequest.of(page, size))
                .map(MembershipResponse::from);
    }

    private MembershipResponse changeTier(Long membershipId, Long newTierId, MembershipStatus closeStatus) {
        UserMembership current = getActiveMembership(membershipId);
        Tier newTier = tierRepository.findById(newTierId)
                .orElseThrow(() -> new IllegalArgumentException("Tier not found: " + newTierId));

        int currentRank = current.getTier().getRank();
        int newRank = newTier.getRank();
        if (newRank == currentRank) throw new IllegalStateException("Already on tier: " + newTier.getName());

        MembershipStatus expectedStatus = newRank > currentRank ? MembershipStatus.UPGRADED : MembershipStatus.DOWNGRADED;
        if (expectedStatus != closeStatus) {
            throw new IllegalStateException(
                "Cannot %s from %s to %s".formatted(closeStatus, current.getTier().getName(), newTier.getName()));
        }

        if (!tierEvaluationService.qualifiesFor(newTier, toMetrics(current.getUser()))) {
            throw new IllegalStateException("User does not qualify for tier: " + newTier.getName());
        }

        SubscriptionStateMachine.transition(current.getStatus(), closeStatus);
        String oldTierName = current.getTier().getName();
        current.setStatus(closeStatus);
        membershipRepository.save(current);

        UserMembership next = buildMembership(current.getUser(), current.getPlan(), newTier);
        membershipRepository.save(next);
        recordHistory(next.getId(), closeStatus.name(), oldTierName, newTier.getName(), null);
        return MembershipResponse.from(next);
    }

    private UserMembership getActiveMembership(Long membershipId) {
        UserMembership m = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found: " + membershipId));
        if (m.getStatus() != MembershipStatus.ACTIVE)
            throw new IllegalStateException("Membership is not active");
        return m;
    }

    private UserMembership buildMembership(User user, MembershipPlan plan, Tier tier) {
        UserMembership m = new UserMembership();
        m.setUser(user); m.setPlan(plan); m.setTier(tier);
        m.setStatus(MembershipStatus.ACTIVE);
        m.setStartDate(LocalDate.now());
        m.setExpiryDate(LocalDate.now().plusDays(plan.getPlanType().durationDays));
        return m;
    }

    private void recordHistory(Long membershipId, String action, String oldTier, String newTier, String remarks) {
        historyRepository.save(MembershipHistory.builder()
                .userMembershipId(membershipId)
                .action(action).oldTier(oldTier).newTier(newTier)
                .remarks(remarks).createdAt(LocalDateTime.now())
                .build());
    }

    public UserMetrics toMetrics(User user) {
        return new UserMetrics(
                user.getTotalOrders(),
                BigDecimal.valueOf(user.getMonthlyOrderValue()),
                user.getCohort(),
                12
        );
    }
}
