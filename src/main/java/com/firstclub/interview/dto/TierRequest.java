package com.firstclub.interview.dto;

import com.firstclub.interview.model.Benefit;
import com.firstclub.interview.model.Rule;
import java.util.List;

public record TierRequest(String name, Integer rank, Rule qualificationRule, List<Benefit> benefits) {}
