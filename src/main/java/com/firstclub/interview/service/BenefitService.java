package com.firstclub.interview.service;

import com.firstclub.interview.model.Benefit;
import java.util.List;

public interface BenefitService {
    List<Benefit> getBenefits(Long userId);
}
