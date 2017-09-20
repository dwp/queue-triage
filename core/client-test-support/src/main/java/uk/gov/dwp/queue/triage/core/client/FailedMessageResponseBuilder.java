package uk.gov.dwp.queue.triage.core.domain;

import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageResponseBuilder {

    private FailedMessageId failedMessageId;
    private String broker;
    private Optional<String> destination = Optional.empty();
    private Instant sentAt;
    private Instant failedAt;
    private String content;
    private FailedMessageStatus currentStatus;
    private Map<String, Object> properties = new HashMap<>();
    private Set<String> labels = new HashSet<>();

    private FailedMessageResponseBuilder() {
    }

    public static FailedMessageResponseBuilder newFailedMessage() {
        return new FailedMessageResponseBuilder()
                .withFailedMessageId(newFailedMessageId())
                .withStatus(FailedMessageStatus.FAILED);
    }

    public FailedMessageResponse build() {
        return new FailedMessageResponse(
                failedMessageId,
                broker,
                destination,
                sentAt,
                failedAt,
                content,
                currentStatus,
                properties,
                labels);
    }

    public FailedMessageResponseBuilder withNewFailedMessageId() {
        this.failedMessageId = newFailedMessageId();
        return this;
    }

    public FailedMessageResponseBuilder withFailedMessageId(FailedMessageId failedMessageId) {
        this.failedMessageId = failedMessageId;
        return this;
    }

    public FailedMessageResponseBuilder withBroker(String broker) {
        this.broker = broker;
        return this;
    }

    public FailedMessageResponseBuilder withDestination(String destination) {
        this.destination = Optional.ofNullable(destination);
        return this;
    }

    public FailedMessageResponseBuilder withDestination(Optional<String> destination) {
        this.destination = destination;
        return this;
    }

    public FailedMessageResponseBuilder withSentAt(Instant sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public FailedMessageResponseBuilder withFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
        return this;
    }

    public FailedMessageResponseBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public FailedMessageResponseBuilder withProperties(Map<String, Object> properties) {
        this.properties = (properties != null) ? properties : new HashMap<>();
        return this;
    }

    public FailedMessageResponseBuilder withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public FailedMessageResponseBuilder withStatus(FailedMessageStatus status) {
        this.currentStatus = status;
        return this;
    }

    public FailedMessageResponseBuilder withLabels(Set<String> labels) {
        this.labels = (labels != null) ? labels : new HashSet<>();
        return this;
    }

    public FailedMessageResponseBuilder withLabel(String label) {
        this.labels.add(label);
        return this;
    }
}
