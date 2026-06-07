package com.firstclub.interview.model;

import com.firstclub.interview.enums.LogicalOperator;
import lombok.Data;
import java.util.List;

@Data
public class CompositeRule implements Rule {
    private LogicalOperator operator;
    private List<Rule> rules;
}
