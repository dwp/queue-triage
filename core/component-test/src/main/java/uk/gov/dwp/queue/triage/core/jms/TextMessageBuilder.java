package uk.gov.dwp.queue.triage.core.jms;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

public class TextMessageBuilder {

    private String content;
    private Map<String, Object> properties = new HashMap<>();

    public TextMessageBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public TextMessageBuilder withProperty(String key, Object value) {
        if (properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
        return this;
    }

    public TextMessageBuilder withProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public TextMessage build(Session session) throws JMSException {
        final TextMessage textMessage = session.createTextMessage(content);
        if (properties != null) {
            for (String key : properties.keySet()) {
                textMessage.setObjectProperty(key, properties.get(key));
            }
        }
        return textMessage;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("with content '")
                .append(content).append("'");
        if (!properties.isEmpty()) {
            for (String key : properties.keySet()) {
                sb.append(" and property ").append(key).append(" '").append(properties.get(key)).append("'");
            }
        }
        return sb.toString();
    }
}
