package com.firstclub.interview.rule;

import com.firstclub.interview.enums.LogicalOperator;
import com.firstclub.interview.enums.Operator;
import com.firstclub.interview.model.ConditionRule;
import com.firstclub.interview.model.CompositeRule;
import com.firstclub.interview.model.UserMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonRuleEvaluatorTest {

    private JsonRuleEvaluator evaluator;
    private UserMetrics goldUser;    // 8 orders, ₹6000
    private UserMetrics silverUser;  // 2 orders, ₹500

    @BeforeEach
    void setUp() {
        evaluator = new JsonRuleEvaluator();
        goldUser   = new UserMetrics(8,  BigDecimal.valueOf(6000), "REGULAR", 12);
        silverUser = new UserMetrics(2,  BigDecimal.valueOf(500),  "REGULAR", 3);
    }

    @Test
    void nullRule_alwaysQualifies() {
        assertTrue(evaluator.evaluate(null, silverUser));
    }

    @Test
    void conditionRule_orderCount_gte_passes() {
        ConditionRule rule = new ConditionRule();
        rule.setField("monthlyOrderCount"); rule.setOperator(Operator.GTE); rule.setValue(5);
        assertTrue(evaluator.evaluate(rule, goldUser));
        assertFalse(evaluator.evaluate(rule, silverUser));
    }

    @Test
    void compositeRule_AND_requiresAllConditions() {
        CompositeRule rule = new CompositeRule();
        rule.setOperator(LogicalOperator.AND);
        rule.setRules(List.of(condition("monthlyOrderCount", Operator.GTE, 5),
                              condition("monthlyOrderValue",  Operator.GTE, 5000)));

        assertTrue(evaluator.evaluate(rule, goldUser));    // 8 orders, ₹6000 ✓
        assertFalse(evaluator.evaluate(rule, silverUser)); // 2 orders, ₹500  ✗
    }

    @Test
    void compositeRule_OR_passesIfAnyMatches() {
        CompositeRule rule = new CompositeRule();
        rule.setOperator(LogicalOperator.OR);
        rule.setRules(List.of(condition("cohort", Operator.IN, List.of("VIP")),
                              condition("monthlyOrderCount", Operator.GTE, 5)));

        UserMetrics vipUser = new UserMetrics(1, BigDecimal.valueOf(100), "VIP", 1);
        assertTrue(evaluator.evaluate(rule, vipUser));     // VIP cohort matches
        assertTrue(evaluator.evaluate(rule, goldUser));    // order count matches
        assertFalse(evaluator.evaluate(rule, silverUser)); // neither matches
    }

    @Test
    void nestedCompositeRule_evaluatesRecursively() {
        // (orderCount >= 20 AND orderValue >= 20000) OR (cohort IN [VIP] AND orderValue >= 10000)
        CompositeRule platinumRule = new CompositeRule();
        platinumRule.setOperator(LogicalOperator.OR);

        CompositeRule highVolume = new CompositeRule();
        highVolume.setOperator(LogicalOperator.AND);
        highVolume.setRules(List.of(condition("monthlyOrderCount", Operator.GTE, 20),
                                    condition("monthlyOrderValue",  Operator.GTE, 20000)));

        CompositeRule vipHighSpend = new CompositeRule();
        vipHighSpend.setOperator(LogicalOperator.AND);
        vipHighSpend.setRules(List.of(condition("cohort", Operator.IN, List.of("VIP")),
                                      condition("monthlyOrderValue", Operator.GTE, 10000)));

        platinumRule.setRules(List.of(highVolume, vipHighSpend));

        UserMetrics platUser = new UserMetrics(25, BigDecimal.valueOf(25000), "VIP", 12);
        UserMetrics vipLowSpend = new UserMetrics(1, BigDecimal.valueOf(5000), "VIP", 12);

        assertTrue(evaluator.evaluate(platinumRule, platUser));    // both branches match
        assertFalse(evaluator.evaluate(platinumRule, vipLowSpend)); // VIP but spend < 10000
    }

    private ConditionRule condition(String field, Operator op, Object value) {
        ConditionRule r = new ConditionRule();
        r.setField(field); r.setOperator(op); r.setValue(value);
        return r;
    }
}
