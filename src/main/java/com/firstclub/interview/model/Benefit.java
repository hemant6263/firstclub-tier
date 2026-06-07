package com.firstclub.interview.model;

import com.firstclub.interview.enums.BenefitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Benefit {
    private BenefitType type;
    private String value;  // e.g. "10" for 10% discount, null for boolean benefits
}
