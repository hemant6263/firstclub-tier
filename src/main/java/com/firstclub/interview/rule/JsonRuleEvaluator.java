package com.firstclub.interview.rule;

import com.firstclub.interview.enums.LogicalOperator;
import com.firstclub.interview.enums.Operator;
import com.firstclub.interview.model.ConditionRule;
import com.firstclub.interview.model.CompositeRule;
import com.firstclub.interview.model.Rule;
import com.firstclub.interview.model.UserMetrics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JsonRuleEvaluator implements RuleEvaluator {

    @Override
    public boolean evaluate(Rule rule, UserMetrics metrics) {
        if (rule == null) return true;

        if (rule instanceof ConditionRule c) {
            return evaluateCondition(c, metrics);
        }

        CompositeRule composite = (CompositeRule) rule;
        return composite.getOperator() == LogicalOperator.AND
                ? composite.getRules().stream().allMatch(r -> evaluate(r, metrics))
                : composite.getRules().stream().anyMatch(r -> evaluate(r, metrics));
    }

    private boolean evaluateCondition(ConditionRule rule, UserMetrics metrics) {
        Object rawValue = getField(rule.getField(), metrics);
        Operator op = rule.getOperator();

        if (op == Operator.IN || op == Operator.NOT_IN) {
            List<?> list = (List<?>) rule.getValue();
            boolean contains = list.stream().map(Object::toString)
                    .anyMatch(v -> v.equalsIgnoreCase(rawValue.toString()));
            return op == Operator.IN ? contains : !contains;
        }

        BigDecimal actual = toBigDecimal(rawValue);
        BigDecimal expected = toBigDecimal(rule.getValue());
        int cmp = actual.compareTo(expected);
        return switch (op) {
            case EQ  -> cmp == 0;
            case GT  -> cmp > 0;
            case GTE -> cmp >= 0;
            case LT  -> cmp < 0;
            case LTE -> cmp <= 0;
            default  -> false;
        };
    }

    private Object getField(String field, UserMetrics m) {
        return switch (field) {
            case "monthlyOrderCount"   -> m.getMonthlyOrderCount();
            case "monthlyOrderValue"   -> m.getMonthlyOrderValue();
            case "cohort"              -> m.getCohort();
            case "accountAgeInMonths"  -> m.getAccountAgeInMonths();
            default -> throw new IllegalArgumentException("Unknown metric field: " + field);
        };
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(val.toString());
    }
}
