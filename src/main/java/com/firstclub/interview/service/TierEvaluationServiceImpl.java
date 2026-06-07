package com.firstclub.interview.service;

import com.firstclub.interview.entity.Tier;
import com.firstclub.interview.model.UserMetrics;
import com.firstclub.interview.repository.TierRepository;
import com.firstclub.interview.rule.RuleEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TierEvaluationServiceImpl implements TierEvaluationService {

    private final TierRepository tierRepository;
    private final RuleEvaluator ruleEvaluator;

    @Override
    public Optional<Tier> determineTier(UserMetrics metrics) {
        return tierRepository.findAllByOrderByRankAsc().stream()
                .sorted((a, b) -> b.getRank() - a.getRank())
                .filter(tier -> qualifiesFor(tier, metrics))
                .findFirst();
    }

    @Override
    public boolean qualifiesFor(Tier tier, UserMetrics metrics) {
        return ruleEvaluator.evaluate(tier.getQualificationRule(), metrics);
    }
}
