package com.firstclub.interview.service;

import com.firstclub.interview.entity.Tier;
import com.firstclub.interview.model.UserMetrics;
import java.util.Optional;

public interface TierEvaluationService {
    /** Returns highest tier the user qualifies for based on their metrics. */
    Optional<Tier> determineTier(UserMetrics metrics);
    boolean qualifiesFor(Tier tier, UserMetrics metrics);
}
