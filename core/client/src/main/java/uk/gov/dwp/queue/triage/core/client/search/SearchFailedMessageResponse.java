package uk.gov.dwp.queue.triage.core.client.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SearchFailedMessageResponse {

    @NotNull
    private final FailedMessageId failedMessageId;
    private final String jmsMessageId;
    @NotNull
    private final String broker;
    private final String destination;
    private final FailedMessageStatus status;
    private final Instant statusDateTime;
    private final String content;
    private final Set<String> labels;

    public static SearchFailedMessageResponseBuilder newSearchFailedMessageResponse() {
        return new SearchFailedMessageResponseBuilder();
    }

    private SearchFailedMessageResponse(@JsonProperty("failedMessageId") FailedMessageId failedMessageId,
                                        @JsonProperty("jmsMessageId") String jmsMessageId,
                                        @JsonProperty("broker") String broker,
                                        @JsonProperty("destination") String destination,
                                        @JsonProperty("status") FailedMessageStatus status,
                                        @JsonProperty("statusDateTime") Instant statusDateTime,
                                        @JsonProperty("content") String content,
                                        @JsonProperty("labels") Set<String> labels) {
        this.failedMessageId = failedMessageId;
        this.jmsMessageId = jmsMessageId;
        this.broker = broker;
        this.destination = destination;
        this.status = status;
        this.statusDateTime = statusDateTime;
        this.content = content;
        this.labels = labels;
    }

    public FailedMessageId getFailedMessageId() {
        return failedMessageId;
    }

    public String getJmsMessageId() {
        return jmsMessageId;
    }

    public String getBroker() {
        return broker;
    }

    public Optional<String> getDestination() {
        return Optional.ofNullable(destination);
    }

    public FailedMessageStatus getStatus() {
        return status;
    }

    public Instant getStatusDateTime() {
        return statusDateTime;
    }

    public String getContent() {
        return content;
    }

    public Set<String> getLabels() {
        return labels;
    }

    @Override
    public String toString() {
        return "SearchFailedMessageResponse{" +
                "failedMessageId=" + failedMessageId +
                ", jmsMessageId='" + jmsMessageId + '\'' +
                ", broker='" + broker + '\'' +
                ", destination=" + destination +
                ", status=" + status +
                ", statusDateTime=" + statusDateTime +
                ", content='" + content + '\'' +
                ", labels=" + labels +
                '}';
    }

    public static class SearchFailedMessageResponseBuilder {

        private FailedMessageId failedMessageId;
        private String jmsMessageId;
        private String broker;
        private String destination;
        private FailedMessageStatus status;
        private Instant statusDateTime;
        private String content;
        private Set<String> labels = new HashSet<>();

        private SearchFailedMessageResponseBuilder() {
        }

        public SearchFailedMessageResponseBuilder withFailedMessageId(FailedMessageId failedMessageId) {
            this.failedMessageId = failedMessageId;
            return this;
        }

        public SearchFailedMessageResponseBuilder withJmsMessageId(String jmsMessageId) {
            this.jmsMessageId = jmsMessageId;
            return this;
        }

        public SearchFailedMessageResponseBuilder withBroker(String broker) {
            this.broker = broker;
            return this;
        }

        public SearchFailedMessageResponseBuilder withDestination(String destination) {
            this.destination = destination;
            return this;
        }

        public SearchFailedMessageResponseBuilder withStatus(FailedMessageStatus status) {
            this.status = status;
            return this;
        }

        public SearchFailedMessageResponseBuilder withStatusDateTime(Instant statusDateTime) {
            this.statusDateTime = statusDateTime;
            return this;
        }

        public SearchFailedMessageResponseBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public SearchFailedMessageResponseBuilder withLabels(Set<String> labels) {
            this.labels = labels;
            return this;
        }

        public SearchFailedMessageResponse build() {
            return new SearchFailedMessageResponse(failedMessageId, jmsMessageId, broker, destination, status, statusDateTime, content, labels);
        }
    }
}
