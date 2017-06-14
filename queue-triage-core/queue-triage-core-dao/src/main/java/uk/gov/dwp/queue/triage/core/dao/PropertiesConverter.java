package uk.gov.dwp.queue.triage.core.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class PropertiesConverter implements ObjectConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper;

    public PropertiesConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@class");
    }

    @Override
    public Map<String, Object> convertToObject(String value) {
        try {
            return objectMapper.readValue(value, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String convertFromObject(Map<String, Object> item) {
        if (item == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
