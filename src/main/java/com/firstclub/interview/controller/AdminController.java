package com.firstclub.interview.controller;

import com.firstclub.interview.dto.ApiResponse;
import com.firstclub.interview.dto.TierRequest;
import com.firstclub.interview.entity.Tier;
import com.firstclub.interview.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/tiers")
@RequiredArgsConstructor
public class AdminController {

    private final TierRepository tierRepository;

    @PostMapping
    public ResponseEntity<?> createTier(@RequestBody TierRequest req) {
        if (tierRepository.findByName(req.name()).isPresent()) {
            throw new IllegalArgumentException("Tier already exists: " + req.name());
        }
        Tier tier = new Tier();
        tier.setName(req.name()); tier.setRank(req.rank());
        tier.setQualificationRule(req.qualificationRule()); tier.setBenefits(req.benefits());
        return ResponseEntity.ok(ApiResponse.ok(tierRepository.save(tier)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTier(@PathVariable Long id, @RequestBody TierRequest req) {
        Tier tier = tierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tier not found: " + id));
        tier.setQualificationRule(req.qualificationRule());
        tier.setBenefits(req.benefits()); tier.setRank(req.rank());
        return ResponseEntity.ok(ApiResponse.ok(tierRepository.save(tier)));
    }
}
