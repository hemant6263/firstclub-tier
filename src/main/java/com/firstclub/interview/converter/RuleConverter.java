package com.firstclub.interview.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.interview.model.Rule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RuleConverter implements AttributeConverter<Rule, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Rule rule) {
        try { return mapper.writeValueAsString(rule); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public Rule convertToEntityAttribute(String json) {
        if (json == null) return null;
        try { return mapper.readValue(json, Rule.class); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
