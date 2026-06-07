package com.firstclub.interview.enums;

public enum PlanType {
    MONTHLY(30),
    QUARTERLY(90),
    YEARLY(365);

    public final int durationDays;
    PlanType(int durationDays) { this.durationDays = durationDays; }
}
