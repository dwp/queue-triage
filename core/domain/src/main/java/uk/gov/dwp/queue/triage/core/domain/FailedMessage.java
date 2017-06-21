package uk.gov.dwp.queue.triage.core.domain;

import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class FailedMessage {

    @NotNull
    private final FailedMessageId failedMessageId;
    private final Destination destination;
    private final ZonedDateTime sentAt;
    private final ZonedDateTime failedAt;
    @NotEmpty
    private final String content;
    @NotNull
    private final Map<String, Object> properties;

    FailedMessage(FailedMessageId failedMessageId,
                  Destination destination,
                  ZonedDateTime sentAt,
                  ZonedDateTime failedAt,
                  String content,
                  Map<String, Object> properties) {
        this.failedMessageId = failedMessageId;
        this.destination = destination;
        this.sentAt = sentAt;
        this.failedAt = failedAt;
        this.content = content;
        this.properties = properties;
    }

    public FailedMessageId getFailedMessageId() {
        return failedMessageId;
    }

    public Destination getDestination() {
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
        return (T)properties.get(name);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
