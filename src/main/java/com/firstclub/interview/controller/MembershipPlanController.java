package com.firstclub.interview.controller;

import com.firstclub.interview.dto.ApiResponse;
import com.firstclub.interview.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/memberships")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;

    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        return ResponseEntity.ok(ApiResponse.ok(planService.getAllPlans()));
    }

    @GetMapping("/tiers")
    public ResponseEntity<?> getTiers() {
        return ResponseEntity.ok(ApiResponse.ok(planService.getAllTiers()));
    }
}
