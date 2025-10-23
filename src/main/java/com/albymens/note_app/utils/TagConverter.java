package com.albymens.note_app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class TagConverter implements AttributeConverter<List<String>, String> {
    private static final Logger logger = LoggerFactory.getLogger(TagConverter.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> strings) {

        List<String> normalized = normalizeTags(strings);

        try {
            return mapper.writeValueAsString(normalized);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing tags to JSON: {}", e.getMessage());
            throw new IllegalArgumentException("Error serializing tags to JSON");
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            logger.error("Error reading tags from JSON: {}", e.getMessage());
            throw new IllegalArgumentException("Error reading tags from JSON");
        }
    }

    public static String normalizeTag(String tag) {
        return tag == null ? null : tag.trim().toLowerCase();
    }

    public static List<String> normalizeTags(List<String> tags) {
        if (tags == null) return Collections.emptyList();

        return tags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(TagConverter::normalizeTag)
                .distinct()
                .collect(Collectors.toList());
    }
}
