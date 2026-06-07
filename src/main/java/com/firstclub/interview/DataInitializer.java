package com.firstclub.interview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.interview.entity.*;
import com.firstclub.interview.enums.*;
import com.firstclub.interview.model.*;
import com.firstclub.interview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MembershipPlanRepository planRepo;
    private final TierRepository tierRepo;
    private final UserRepository userRepo;

    @Override
    public void run(String... args) {
        seedPlans();
        seedTiers();
        seedUsers();
    }

    private void seedPlans() {
        createPlan(PlanType.MONTHLY,   199.0,  "Monthly membership");
        createPlan(PlanType.QUARTERLY, 499.0,  "Quarterly membership - save 16%");
        createPlan(PlanType.YEARLY,    1499.0, "Yearly membership - save 37%");
    }

    private void createPlan(PlanType type, double price, String desc) {
        if (planRepo.findByPlanType(type).isEmpty()) {
            MembershipPlan p = new MembershipPlan();
            p.setPlanType(type); p.setPrice(price); p.setDescription(desc);
            planRepo.save(p);
        }
    }

    private void seedTiers() {
        createTier("SILVER", 1, null,
            List.of(new Benefit(BenefitType.DISCOUNT_PERCENTAGE, "5")));

        createTier("GOLD", 2,
            or(
                and(gte("monthlyOrderCount", 5), gte("monthlyOrderValue", 5000)),
                in("cohort", List.of("VIP"))
            ),
            List.of(new Benefit(BenefitType.DISCOUNT_PERCENTAGE, "10"),
                    new Benefit(BenefitType.FREE_DELIVERY, null),
                    new Benefit(BenefitType.EARLY_ACCESS, null)));

        createTier("PLATINUM", 3,
            or(
                and(gte("monthlyOrderCount", 20), gte("monthlyOrderValue", 20000)),
                and(in("cohort", List.of("VIP")), gte("monthlyOrderValue", 10000))
            ),
            List.of(new Benefit(BenefitType.DISCOUNT_PERCENTAGE, "20"),
                    new Benefit(BenefitType.FREE_DELIVERY, null),
                    new Benefit(BenefitType.PRIORITY_SUPPORT, null),
                    new Benefit(BenefitType.EARLY_ACCESS, null)));
    }

    private void createTier(String name, int rank, Rule rule, List<Benefit> benefits) {
        if (tierRepo.findByName(name).isEmpty()) {
            Tier t = new Tier();
            t.setName(name); t.setRank(rank);
            t.setQualificationRule(rule); t.setBenefits(benefits);
            tierRepo.save(t);
        }
    }

    private void seedUsers() {
        createUser("Alice",   "alice@example.com",   "VIP",     25, 25000); // → PLATINUM
        createUser("Bob",     "bob@example.com",     "REGULAR", 8,  6000);  // → GOLD
        createUser("Charlie", "charlie@example.com", "REGULAR", 2,  500);   // → SILVER
    }

    private void createUser(String name, String email, String cohort, int orders, double spend) {
        User u = new User();
        u.setName(name); u.setEmail(email); u.setCohort(cohort);
        u.setTotalOrders(orders); u.setMonthlyOrderValue(spend);
        userRepo.save(u);
    }

    private CompositeRule and(Rule... rules) {
        CompositeRule r = new CompositeRule();
        r.setOperator(LogicalOperator.AND); r.setRules(List.of(rules)); return r;
    }
    private CompositeRule or(Rule... rules) {
        CompositeRule r = new CompositeRule();
        r.setOperator(LogicalOperator.OR); r.setRules(List.of(rules)); return r;
    }
    private ConditionRule gte(String field, Object value) {
        ConditionRule r = new ConditionRule();
        r.setField(field); r.setOperator(Operator.GTE); r.setValue(value); return r;
    }
    private ConditionRule in(String field, Object value) {
        ConditionRule r = new ConditionRule();
        r.setField(field); r.setOperator(Operator.IN); r.setValue(value); return r;
    }
}
