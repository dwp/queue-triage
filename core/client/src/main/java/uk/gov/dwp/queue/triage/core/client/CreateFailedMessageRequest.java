package uk.gov.dwp.queue.triage.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Map;

public class CreateFailedMessageRequest {

    private final FailedMessageId failedMessageId;
    private final String brokerName;
    private final String destination;
    private final Instant sentAt;
    private final Instant failedAt;
    private final String content;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private final Map<String, Object> properties;

    CreateFailedMessageRequest(@JsonProperty("failedMessageId") FailedMessageId failedMessageId,
                               @JsonProperty("brokerName") String brokerName,
                               @JsonProperty("destinationName") String destination,
                               @JsonProperty("sentAt") Instant sentAt,
                               @JsonProperty("failedAt") Instant failedAt,
                               @JsonProperty("content") String content,
                               @JsonProperty("properties") Map<String, Object> properties) {
        this.failedMessageId = failedMessageId;
        this.brokerName = brokerName;
        this.destination = destination;
        this.sentAt = sentAt;
        this.failedAt = failedAt;
        this.content = content;
        this.properties = properties;
    }

    public static CreateFailedMessageRequestBuilder newCreateFailedMessageRequest() {
        return new CreateFailedMessageRequestBuilder();
    }

    public FailedMessageId getFailedMessageId() {
        return failedMessageId;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getDestinationName() {
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

    public static class CreateFailedMessageRequestBuilder {
        private FailedMessageId failedMessageId;
        private String brokerName;
        private String destinationName;
        private Instant sentDateTime;
        private Instant failedDateTime;
        private String content;
        private Map<String, Object> properties;

        private CreateFailedMessageRequestBuilder() {
        }

        public CreateFailedMessageRequest build() {
            return new CreateFailedMessageRequest(failedMessageId, brokerName, destinationName, sentDateTime, failedDateTime, content, properties);
        }

        public CreateFailedMessageRequestBuilder withFailedMessageId(FailedMessageId failedMessageId) {
            this.failedMessageId = failedMessageId;
            return this;
        }

        public CreateFailedMessageRequestBuilder withBrokerName(String brokerName) {
            this.brokerName = brokerName;
            return this;
        }

        public CreateFailedMessageRequestBuilder withDestinationName(String destinationName) {
            this.destinationName = destinationName;
            return this;
        }

        public CreateFailedMessageRequestBuilder withSentDateTime(Instant sentDateTime) {
            this.sentDateTime = sentDateTime;
            return this;
        }

        public CreateFailedMessageRequestBuilder withFailedDateTime(Instant failedDateTime) {
            this.failedDateTime = failedDateTime;
            return this;
        }

        public CreateFailedMessageRequestBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public CreateFailedMessageRequestBuilder withProperties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public CreateFailedMessageRequestBuilder withProperty(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }
    }
}
