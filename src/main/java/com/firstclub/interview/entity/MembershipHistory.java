package com.firstclub.interview.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userMembershipId;
    private String action;    // SUBSCRIBED, UPGRADED, DOWNGRADED, CANCELLED, EXPIRED
    private String oldTier;
    private String newTier;
    private String remarks;
    private LocalDateTime createdAt;
}
