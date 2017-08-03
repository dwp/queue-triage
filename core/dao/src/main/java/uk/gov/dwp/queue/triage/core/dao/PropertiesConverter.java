package uk.gov.dwp.queue.triage.core.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class PropertiesConverter implements ObjectConverter<Map<String, Object>, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConverter.class);

    private final ObjectMapper objectMapper;

    public PropertiesConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@class");
    }

    @Override
    public Map<String, Object> convertToObject(String value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            LOGGER.error("Could read the following properties: " + value, e);
        }
        return Collections.emptyMap();
    }

    @Override
    public String convertFromObject(Map<String, Object> item) {
        if (item == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not convert the following Map: " + item, e);
        }
        return null;
    }
}
