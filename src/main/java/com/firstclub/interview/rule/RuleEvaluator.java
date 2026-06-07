package com.firstclub.interview.rule;

import com.firstclub.interview.model.Rule;
import com.firstclub.interview.model.UserMetrics;

public interface RuleEvaluator {
    boolean evaluate(Rule rule, UserMetrics metrics);
}
