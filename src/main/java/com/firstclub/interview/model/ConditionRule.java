package com.firstclub.interview.model;

import com.firstclub.interview.enums.Operator;
import lombok.Data;

@Data
public class ConditionRule implements Rule {
    private String field;       // monthlyOrderCount, monthlyOrderValue, cohort, accountAgeInMonths
    private Operator operator;
    private Object value;       // number, string, or list for IN/NOT_IN
}
