package uk.gov.dwp.queue.triage.core.domain;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class FailedMessageBuilder {

    private FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
    private Destination destination;
    private ZonedDateTime sentDateTime;
    private ZonedDateTime failedDateTime;
    private String content;
    private Map<String, Object> properties = new HashMap<>();

    private FailedMessageBuilder() {
    }

    public static FailedMessageBuilder aFailedMessage() {
        return new FailedMessageBuilder();
    }

    public static FailedMessageBuilder clone(FailedMessage failedMessage) {
        return aFailedMessage()
                .withFailedMessageId(failedMessage.getFailedMessageId())
                .withDestination(failedMessage.getDestination())
                .withSentDateTime(failedMessage.getSentAt())
                .withFailedDateTime(failedMessage.getFailedAt())
                .withContent(failedMessage.getContent())
                .withProperties(failedMessage.getProperties());
    }

    public FailedMessage build() {
        return new FailedMessage(failedMessageId, destination, sentDateTime, failedDateTime, content, properties);
    }

    public FailedMessageBuilder withFailedMessageId(FailedMessageId failedMessageId) {
        this.failedMessageId = failedMessageId;
        return this;
    }

    public FailedMessageBuilder withDestination(Destination destination) {
        this.destination = destination;
        return this;
    }

    public FailedMessageBuilder withSentDateTime(ZonedDateTime sentDateTime) {
        this.sentDateTime = sentDateTime;
        return this;
    }

    public FailedMessageBuilder withFailedDateTime(ZonedDateTime failedDateTime) {
        this.failedDateTime = failedDateTime;
        return this;
    }

    public FailedMessageBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public FailedMessageBuilder withProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public FailedMessageBuilder withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }
}
