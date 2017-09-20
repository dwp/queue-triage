package uk.gov.dwp.queue.triage.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class FailedMessageResponse {

    private final FailedMessageId failedMessageId;
    private final String broker;
    private final Optional<String> destination;
    private final Instant sentAt;
    private final Instant failedAt;
    private final String content;
    private final FailedMessageStatus currentStatus;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private final Map<String, Object> properties;
    private final Set<String> labels;

    public FailedMessageResponse(@JsonProperty("failedMessageId") FailedMessageId failedMessageId,
                                 @JsonProperty("broker") String broker,
                                 @JsonProperty("destination") Optional<String> destination,
                                 @JsonProperty("sentAt") Instant sentAt,
                                 @JsonProperty("failedAt") Instant failedAt,
                                 @JsonProperty("content") String content,
                                 @JsonProperty("currentStatus") FailedMessageStatus currentStatus,
                                 @JsonProperty("properties") Map<String, Object> properties,
                                 @JsonProperty("labels") Set<String> labels) {
        this.failedMessageId = failedMessageId;
        this.broker = broker;
        this.destination = destination;
        this.sentAt = sentAt;
        this.failedAt = failedAt;
        this.content = content;
        this.currentStatus = currentStatus;
        this.properties = properties;
        this.labels = labels;
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

    public FailedMessageStatus getCurrentStatus() {
        return currentStatus;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public <T> T getProperty(String name) {
        return (T) properties.get(name);
    }

    public Set<String> getLabels() {
        return new HashSet<>(labels);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
