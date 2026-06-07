package com.firstclub.interview.entity;

import com.firstclub.interview.converter.BenefitListConverter;
import com.firstclub.interview.converter.RuleConverter;
import com.firstclub.interview.model.Benefit;
import com.firstclub.interview.model.Rule;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Tier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;   // SILVER, GOLD, PLATINUM, DIAMOND...

    private Integer rank;  // for upgrade/downgrade direction comparison

    @Convert(converter = RuleConverter.class)
    @Column(columnDefinition = "TEXT")
    private Rule qualificationRule;  // null = always qualifies (e.g. SILVER)

    @Convert(converter = BenefitListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Benefit> benefits;
}
