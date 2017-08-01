package uk.gov.dwp.queue.triage.core.domain;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class FailedMessage {

    private final FailedMessageId failedMessageId;
    private final Destination destination;
    private final Instant sentAt;
    private final Instant failedAt;
    private final String content;
    private final Map<String, Object> properties;
    private final FailedMessageStatus failedMessageStatus;

    FailedMessage(FailedMessageId failedMessageId,
                  Destination destination,
                  Instant sentAt,
                  Instant failedAt,
                  String content,
                  Map<String, Object> properties,
                  FailedMessageStatus failedMessageStatus) {
        this.failedMessageId = failedMessageId;
        this.destination = destination;
        this.sentAt = sentAt;
        this.failedAt = failedAt;
        this.content = content;
        this.properties = properties;
        this.failedMessageStatus = failedMessageStatus;
    }

    public FailedMessageId getFailedMessageId() {
        return failedMessageId;
    }

    public Destination getDestination() {
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
        return (T)properties.get(name);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public FailedMessageStatus getFailedMessageStatus() {
        return failedMessageStatus;
    }
}
