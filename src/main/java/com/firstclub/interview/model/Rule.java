package com.firstclub.interview.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConditionRule.class, name = "CONDITION"),
    @JsonSubTypes.Type(value = CompositeRule.class, name = "COMPOSITE")
})
public interface Rule {}
