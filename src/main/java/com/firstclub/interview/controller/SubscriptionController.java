package com.firstclub.interview.controller;

import com.firstclub.interview.dto.ApiResponse;
import com.firstclub.interview.dto.SubscribeRequest;
import com.firstclub.interview.dto.TierChangeRequest;
import com.firstclub.interview.service.BenefitService;
import com.firstclub.interview.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/memberships")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final BenefitService benefitService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscribeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.subscribe(request)));
    }

    @PutMapping("/{id}/upgrade")
    public ResponseEntity<?> upgrade(@PathVariable Long id, @RequestBody TierChangeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.upgradeTier(id, req.tierId())));
    }

    @PutMapping("/{id}/downgrade")
    public ResponseEntity<?> downgrade(@PathVariable Long id, @RequestBody TierChangeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.downgradeTier(id, req.tierId())));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.cancel(id)));
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrent(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.getCurrentMembership(userId)));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam Long userId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.getMembershipHistory(userId, page, size)));
    }

    @GetMapping("/benefits/{userId}")
    public ResponseEntity<?> getBenefits(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(benefitService.getBenefits(userId)));
    }
}
