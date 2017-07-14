package uk.gov.dwp.queue.triage.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class FailedMessageResponse {

    private final FailedMessageId failedMessageId;
    private final String broker;
    private final Optional<String> destination;
    private final Instant sentAt;
    private final Instant failedAt;
    private final String content;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private final Map<String, Object> properties;

    public FailedMessageResponse(@JsonProperty("failedMessageId") FailedMessageId failedMessageId,
                                 @JsonProperty("broker") String broker,
                                 @JsonProperty("destination") Optional<String> destination,
                                 @JsonProperty("sentAt") Instant sentAt,
                                 @JsonProperty("failedAt") Instant failedAt,
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

    public Optional<String> getDestination() {
        return destination;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getFailedAt() {
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
