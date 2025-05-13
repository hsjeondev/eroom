package com.eroom.approval;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Converter
public class JsonToMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Map -> JSON 변환 실패", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) {
                return new HashMap<>();
            }
            // System.out.println("💡 JSON 데이터 역직렬화 시도: " + dbData);

            // 역직렬화 시도
            Map<String, Object> resultMap = objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});

            // 🔍 체크박스 값이 단일 String일 때 배열로 강제 변환
            resultMap.forEach((key, value) -> {
                if (value instanceof String && ((String) value).startsWith("[") && ((String) value).endsWith("]")) {
                    try {
                        // String으로 전달된 JSON 배열을 List로 변환
                        List<String> list = objectMapper.readValue((String) value, new TypeReference<List<String>>() {});
                        resultMap.put(key, list);
                    } catch (Exception e) {
                        // System.err.println("❌ List 변환 실패: " + e.getMessage());
                    }
                }
            });

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("JSON -> Map 변환 실패", e);
        }
    }
}
