package com.firstclub.interview.dto;

import com.firstclub.interview.enums.PlanType;

public record SubscribeRequest(Long userId, PlanType planType, Long tierId) {}
