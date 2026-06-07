package com.firstclub.interview.dto;

import com.firstclub.interview.entity.UserMembership;
import com.firstclub.interview.enums.MembershipStatus;
import com.firstclub.interview.enums.PlanType;
import com.firstclub.interview.model.Benefit;

import java.time.LocalDate;
import java.util.List;

public record MembershipResponse(
        Long membershipId,
        Long userId,
        PlanType planType,
        String tier,
        MembershipStatus status,
        LocalDate startDate,
        LocalDate expiryDate,
        List<Benefit> benefits
) {
    public static MembershipResponse from(UserMembership m) {
        return new MembershipResponse(
                m.getId(), m.getUser().getId(),
                m.getPlan().getPlanType(), m.getTier().getName(),
                m.getStatus(), m.getStartDate(), m.getExpiryDate(),
                m.getTier().getBenefits()
        );
    }
}
