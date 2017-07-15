package uk.gov.dwp.queue.triage.core.resource;

import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.CreateFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Map;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;

public class PersistedFailedMessageBuilder {

    private final CreateFailedMessageClient createFailedMessageClient;
    private final CreateFailedMessageRequestBuilder failedMessageBuilder;

    public PersistedFailedMessageBuilder(CreateFailedMessageClient createFailedMessageClient) {
        this.createFailedMessageClient = createFailedMessageClient;
        this.failedMessageBuilder = newCreateFailedMessageRequest();
    }

    public void exists() {
        build();
    }

    public void build() {
        createFailedMessageClient.create(failedMessageBuilder.build());
    }

    public void doesNotExist() {
        // Do nothing
    }

    public PersistedFailedMessageBuilder withFailedMessageId(FailedMessageId failedMessageId) {
        failedMessageBuilder.withFailedMessageId(failedMessageId);
        return this;
    }

    public PersistedFailedMessageBuilder withDestination(String brokerName, String destinationName) {
        failedMessageBuilder.withBrokerName(brokerName);
        failedMessageBuilder.withDestinationName(destinationName);
        return this;
    }

    public PersistedFailedMessageBuilder withSentDateTime(Instant sentDateTime) {
        failedMessageBuilder.withSentDateTime(sentDateTime);
        return this;
    }

    public PersistedFailedMessageBuilder withFailedDateTime(Instant failedDateTime) {
        failedMessageBuilder.withFailedDateTime(failedDateTime);
        return this;
    }

    public PersistedFailedMessageBuilder withContent(String content) {
        failedMessageBuilder.withContent(content);
        return this;
    }

    public PersistedFailedMessageBuilder withProperties(Map<String, Object> properties) {
        failedMessageBuilder.withProperties(properties);
        return this;
    }

    public PersistedFailedMessageBuilder withProperty(String key, Object value) {
        failedMessageBuilder.withProperty(key, value);
        return this;
    }
}