package uk.gov.dwp.queue.triage.core.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JmsMessagePropertyExtractor implements MessagePropertyExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsMessagePropertyExtractor.class);

    @Override
    public Map<String, Object> extractProperties(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        Map<String, Object> properties = new HashMap<>();
        try {
            for (Enumeration propertyNames = message.getPropertyNames(); propertyNames.hasMoreElements(); ) {
                extractPropertyFromMessage((String) propertyNames.nextElement(), message, properties);
            }
        } catch (JMSException e) {
            LOGGER.error("Could not read propertyNames from message '%s'", message, e);
        }
        return properties;
    }

    private void extractPropertyFromMessage(String propertyName, Message message, Map<String, Object> properties) {
        try {
            properties.put(propertyName, message.getObjectProperty(propertyName));
        } catch (JMSException e) {
            String errorMessage = String.format("Could not extract property: '%s' from Message '%s'", propertyName, message);
            LOGGER.error(errorMessage, e);
        }
    }
}
