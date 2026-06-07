package com.firstclub.interview.service;

import com.firstclub.interview.enums.MembershipStatus;
import com.firstclub.interview.model.Benefit;
import com.firstclub.interview.repository.UserMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {

    private final UserMembershipRepository membershipRepository;

    @Override
    public List<Benefit> getBenefits(Long userId) {
        return membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.ACTIVE)
                .map(m -> m.getTier().getBenefits())
                .orElse(List.of());
    }
}
