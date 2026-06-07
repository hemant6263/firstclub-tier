package com.firstclub.interview.service;

import com.firstclub.interview.dto.MembershipResponse;
import com.firstclub.interview.dto.SubscribeRequest;
import com.firstclub.interview.entity.*;
import com.firstclub.interview.enums.MembershipStatus;
import com.firstclub.interview.enums.PlanType;
import com.firstclub.interview.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock UserMembershipRepository membershipRepo;
    @Mock UserRepository userRepo;
    @Mock MembershipPlanRepository planRepo;
    @Mock TierRepository tierRepo;
    @Mock MembershipHistoryRepository historyRepo;
    @Mock TierEvaluationService tierEvaluationService;

    @InjectMocks SubscriptionService service;

    private User user;
    private MembershipPlan plan;
    private Tier silver, gold, platinum;

    @BeforeEach
    void setUp() {
        user = new User(); user.setId(1L); user.setTotalOrders(10);
        user.setMonthlyOrderValue(8000); user.setCohort("REGULAR");

        plan = new MembershipPlan(); plan.setId(1L); plan.setPlanType(PlanType.MONTHLY);

        silver   = tier(1L, "SILVER",   1);
        gold     = tier(2L, "GOLD",     2);
        platinum = tier(3L, "PLATINUM", 3);
    }

    @Test
    void subscribe_success() {
        when(userRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(membershipRepo.findByUserIdAndStatus(1L, MembershipStatus.ACTIVE)).thenReturn(Optional.empty());
        when(planRepo.findByPlanType(PlanType.MONTHLY)).thenReturn(Optional.of(plan));
        when(tierRepo.findById(2L)).thenReturn(Optional.of(gold));
        when(tierEvaluationService.qualifiesFor(gold, service.toMetrics(user))).thenReturn(true);
        when(membershipRepo.save(any())).thenAnswer(inv -> { UserMembership m = inv.getArgument(0); m.setId(10L); return m; });

        MembershipResponse resp = service.subscribe(new SubscribeRequest(1L, PlanType.MONTHLY, 2L));
        assertEquals("GOLD", resp.tier());
        assertEquals(MembershipStatus.ACTIVE, resp.status());
    }

    @Test
    void subscribe_duplicateThrows() {
        when(userRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        UserMembership existing = new UserMembership(); existing.setStatus(MembershipStatus.ACTIVE);
        when(membershipRepo.findByUserIdAndStatus(1L, MembershipStatus.ACTIVE)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
            () -> service.subscribe(new SubscribeRequest(1L, PlanType.MONTHLY, 2L)));
    }

    @Test
    void upgradeTier_wrongDirection_throws() {
        UserMembership active = activeMembership(gold);
        when(membershipRepo.findById(10L)).thenReturn(Optional.of(active));
        when(tierRepo.findById(1L)).thenReturn(Optional.of(silver));

        assertThrows(IllegalStateException.class, () -> service.upgradeTier(10L, 1L));
    }

    @Test
    void downgradeTier_wrongDirection_throws() {
        UserMembership active = activeMembership(gold);
        when(membershipRepo.findById(10L)).thenReturn(Optional.of(active));
        when(tierRepo.findById(3L)).thenReturn(Optional.of(platinum));

        assertThrows(IllegalStateException.class, () -> service.downgradeTier(10L, 3L));
    }

    @Test
    void cancel_setsStatusCancelled() {
        UserMembership active = activeMembership(gold);
        active.setId(10L);
        when(membershipRepo.findById(10L)).thenReturn(Optional.of(active));
        when(membershipRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MembershipResponse resp = service.cancel(10L);
        assertEquals(MembershipStatus.CANCELLED, resp.status());
    }

    private Tier tier(Long id, String name, int rank) {
        Tier t = new Tier(); t.setId(id); t.setName(name); t.setRank(rank);
        t.setBenefits(java.util.List.of()); return t;
    }

    private UserMembership activeMembership(Tier tier) {
        UserMembership m = new UserMembership();
        m.setUser(user); m.setPlan(plan); m.setTier(tier);
        m.setStatus(MembershipStatus.ACTIVE);
        m.setStartDate(LocalDate.now());
        m.setExpiryDate(LocalDate.now().plusDays(30));
        return m;
    }
}
