package com.firstclub.interview.statemachine;

import com.firstclub.interview.enums.MembershipStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionStateMachineTest {

    @ParameterizedTest
    @EnumSource(value = MembershipStatus.class, names = {"CANCELLED", "EXPIRED", "UPGRADED", "DOWNGRADED"})
    void validTransitions_fromActive(MembershipStatus target) {
        assertDoesNotThrow(() -> SubscriptionStateMachine.transition(MembershipStatus.ACTIVE, target));
    }

    @ParameterizedTest
    @EnumSource(value = MembershipStatus.class, names = {"CANCELLED", "EXPIRED", "UPGRADED", "DOWNGRADED"})
    void invalidTransitions_fromNonActive(MembershipStatus from) {
        assertThrows(IllegalStateException.class,
            () -> SubscriptionStateMachine.transition(from, MembershipStatus.ACTIVE));
    }

    @Test
    void activeToActive_isInvalid() {
        assertThrows(IllegalStateException.class,
            () -> SubscriptionStateMachine.transition(MembershipStatus.ACTIVE, MembershipStatus.ACTIVE));
    }
}
