package com.firstclub.interview.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String cohort;          // e.g. "VIP", "STUDENT"
    private int totalOrders;
    private double monthlyOrderValue;
}
