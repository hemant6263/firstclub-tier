package com.firstclub.interview.entity;

import com.firstclub.interview.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class UserMembership {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private MembershipPlan plan;

    @ManyToOne(optional = false)
    private Tier tier;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    private LocalDate startDate;
    private LocalDate expiryDate;

    @Version
    private Long version;
}
