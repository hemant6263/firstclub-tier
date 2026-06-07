package com.firstclub.interview.job;

import com.firstclub.interview.entity.*;
import com.firstclub.interview.enums.MembershipStatus;
import com.firstclub.interview.repository.MembershipHistoryRepository;
import com.firstclub.interview.repository.UserMembershipRepository;
import com.firstclub.interview.service.SubscriptionService;
import com.firstclub.interview.service.TierEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TierEvaluationJob {

    private final UserMembershipRepository membershipRepository;
    private final TierEvaluationService tierEvaluationService;
    private final SubscriptionService subscriptionService;
    private final MembershipHistoryRepository historyRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void evaluate() {
        log.info("TierEvaluationJob starting at {}", LocalDateTime.now());
        List<UserMembership> active = membershipRepository.findAll().stream()
                .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                .toList();

        for (UserMembership membership : active) {
            try {
                expireIfNeeded(membership);
                if (membership.getStatus() == MembershipStatus.ACTIVE) {
                    reevaluateTier(membership);
                }
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock conflict for membership {}, skipping", membership.getId());
            } catch (Exception e) {
                log.error("Error evaluating membership {}", membership.getId(), e);
            }
        }
        log.info("TierEvaluationJob completed");
    }

    @Transactional
    protected void expireIfNeeded(UserMembership membership) {
        if (membership.getExpiryDate().isBefore(LocalDate.now())) {
            membership.setStatus(MembershipStatus.EXPIRED);
            membershipRepository.save(membership);
            historyRepository.save(MembershipHistory.builder()
                    .userMembershipId(membership.getId())
                    .action("EXPIRED").oldTier(membership.getTier().getName())
                    .createdAt(LocalDateTime.now()).build());
        }
    }

    @Transactional
    protected void reevaluateTier(UserMembership membership) {
        var metrics = subscriptionService.toMetrics(membership.getUser());
        tierEvaluationService.determineTier(metrics).ifPresent(bestTier -> {
            if (!bestTier.getId().equals(membership.getTier().getId())) {
                log.info("User {} tier change: {} -> {}",
                        membership.getUser().getId(), membership.getTier().getName(), bestTier.getName());
                // record the auto tier change
                int newRank = bestTier.getRank();
                int oldRank = membership.getTier().getRank();
                String action = newRank > oldRank ? "UPGRADED" : "DOWNGRADED";
                String oldTier = membership.getTier().getName();
                membership.setTier(bestTier);
                membershipRepository.save(membership);
                historyRepository.save(MembershipHistory.builder()
                        .userMembershipId(membership.getId())
                        .action(action).oldTier(oldTier).newTier(bestTier.getName())
                        .remarks("Auto-evaluated by nightly job").createdAt(LocalDateTime.now())
                        .build());
            }
        });
    }
}
