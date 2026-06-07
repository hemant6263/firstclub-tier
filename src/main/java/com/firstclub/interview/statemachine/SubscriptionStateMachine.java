package com.firstclub.interview.statemachine;

import com.firstclub.interview.enums.MembershipStatus;

import java.util.Map;
import java.util.Set;

public class SubscriptionStateMachine {

    private static final Map<MembershipStatus, Set<MembershipStatus>> TRANSITIONS = Map.of(
        MembershipStatus.ACTIVE, Set.of(
            MembershipStatus.CANCELLED,
            MembershipStatus.EXPIRED,
            MembershipStatus.UPGRADED,
            MembershipStatus.DOWNGRADED
        )
    );

    public static void transition(MembershipStatus from, MembershipStatus to) {
        Set<MembershipStatus> allowed = TRANSITIONS.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new IllegalStateException(
                "Invalid transition: %s → %s".formatted(from, to));
        }
    }
}
