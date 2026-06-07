package com.firstclub.interview.repository;

import com.firstclub.interview.entity.MembershipPlan;
import com.firstclub.interview.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    Optional<MembershipPlan> findByPlanType(PlanType planType);
}
