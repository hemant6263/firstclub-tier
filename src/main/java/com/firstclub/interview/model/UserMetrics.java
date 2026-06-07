package com.firstclub.interview.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserMetrics {
    private int monthlyOrderCount;
    private BigDecimal monthlyOrderValue;
    private String cohort;
    private int accountAgeInMonths;
}
