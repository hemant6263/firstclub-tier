package com.firstclub.interview.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.interview.model.Benefit;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class BenefitListConverter implements AttributeConverter<List<Benefit>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<List<Benefit>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<Benefit> benefits) {
        try { return mapper.writeValueAsString(benefits); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Benefit> convertToEntityAttribute(String json) {
        if (json == null) return List.of();
        try { return mapper.readValue(json, TYPE); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
