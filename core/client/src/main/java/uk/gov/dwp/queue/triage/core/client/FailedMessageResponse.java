package uk.gov.dwp.queue.triage.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class FailedMessageResponse {

    private final FailedMessageId failedMessageId;
    private final String broker;
    private final String destination;
    private final ZonedDateTime sentAt;
    private final ZonedDateTime failedAt;
    private final String content;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private final Map<String, Object> properties;

    FailedMessageResponse(@JsonProperty("failedMessageId") FailedMessageId failedMessageId,
                          @JsonProperty("broker") String broker,
                          @JsonProperty("destination") String destination,
                          @JsonProperty("sentAt") ZonedDateTime sentAt,
                          @JsonProperty("failedAt") ZonedDateTime failedAt,
                          @JsonProperty("content") String content,
                          @JsonProperty("properties") Map<String, Object> properties) {
        this.failedMessageId = failedMessageId;
        this.broker = broker;
        this.destination = destination;
        this.sentAt = sentAt;
        this.failedAt = failedAt;
        this.content = content;
        this.properties = properties;
    }

    public FailedMessageId getFailedMessageId() {
        return failedMessageId;
    }

    public String getBroker() {
        return broker;
    }

    public String getDestination() {
        return destination;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public ZonedDateTime getFailedAt() {
        return failedAt;
    }

    public String getContent() {
        return content;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public <T> T getProperty(String name) {
        return (T) properties.get(name);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

}
