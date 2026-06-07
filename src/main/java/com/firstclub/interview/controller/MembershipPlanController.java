package com.firstclub.interview.controller;

import com.firstclub.interview.decorator.ResponseDecorator;
import com.firstclub.interview.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/memberships")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;
    private final ResponseDecorator decorator;

    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        return decorator.ok(planService.getAllPlans());
    }

    @GetMapping("/tiers")
    public ResponseEntity<?> getTiers() {
        return decorator.ok(planService.getAllTiers());
    }
}
