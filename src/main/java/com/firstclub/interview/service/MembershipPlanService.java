package com.firstclub.interview.service;

import com.firstclub.interview.entity.MembershipPlan;
import com.firstclub.interview.entity.Tier;
import com.firstclub.interview.repository.MembershipPlanRepository;
import com.firstclub.interview.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {
    private final MembershipPlanRepository planRepository;
    private final TierRepository tierRepository;

    public List<MembershipPlan> getAllPlans() { return planRepository.findAll(); }
    public List<Tier> getAllTiers() { return tierRepository.findAllByOrderByRankAsc(); }
}
