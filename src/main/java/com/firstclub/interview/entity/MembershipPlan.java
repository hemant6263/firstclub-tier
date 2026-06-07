package com.firstclub.interview.entity;

import com.firstclub.interview.enums.PlanType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MembershipPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PlanType planType;

    private double price;
    private String description;
}
