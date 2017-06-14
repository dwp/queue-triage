package uk.gov.dwp.queue.triage.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.validator.constraints.NotEmpty;

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
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private final Map<String, Object> properties;

    FailedMessage(@JsonProperty("failedMessageId") FailedMessageId failedMessageId,
                  @JsonProperty("destination") Destination destination,
                  @JsonProperty("sentAt") ZonedDateTime sentAt,
                  @JsonProperty("failedAt") ZonedDateTime failedAt,
                  @JsonProperty("content") String content,
                  @JsonProperty("properties") Map<String, Object> properties) {
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
