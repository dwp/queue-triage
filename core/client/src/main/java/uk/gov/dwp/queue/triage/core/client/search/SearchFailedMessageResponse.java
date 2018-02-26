package uk.gov.dwp.queue.triage.core.client.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public class SearchFailedMessageResponse {

    @NotNull
    private final FailedMessageId failedMessageId;
    private final String jmsMessageId;
    @NotNull
    private final String broker;
    private final Optional<String> destination;
    @NotNull
    private final Instant sentDateTime;
    @NotNull
    private final Instant lastFailedDateTime;
    private final String content;
    private final Set<String> labels;

    public static SearchFailedMessageResponseBuilder newSearchFailedMessageResponse() {
        return new SearchFailedMessageResponseBuilder();
    }

    private SearchFailedMessageResponse(@JsonProperty("failedMessageId") FailedMessageId failedMessageId,
                                        @JsonProperty("jmsMessageId") String jmsMessageId,
                                        @JsonProperty("broker") String broker,
                                        @JsonProperty("destination") Optional<String> destination,
                                        @JsonProperty("sentDateTime") Instant sentDateTime,
                                        @JsonProperty("failedDateTime") Instant lastFailedDateTime,
                                        @JsonProperty("content") String content,
                                        @JsonProperty("labels") Set<String> labels) {
        this.failedMessageId = failedMessageId;
        this.jmsMessageId = jmsMessageId;
        this.broker = broker;
        this.destination = destination;
        this.sentDateTime = sentDateTime;
        this.lastFailedDateTime = lastFailedDateTime;
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
        return destination;
    }

    public Instant getSentDateTime() {
        return sentDateTime;
    }

    public Instant getLastFailedDateTime() {
        return lastFailedDateTime;
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
                ", sentDateTime=" + sentDateTime +
                ", lastFailedDateTime=" + lastFailedDateTime +
                ", content='" + content + '\'' +
                ", labels=" + labels +
                '}';
    }

    public static class SearchFailedMessageResponseBuilder {

        private FailedMessageId failedMessageId;
        private String jmsMessageId;
        private String broker;
        private Optional<String> destination = Optional.empty();
        private Instant sentDateTime;
        private Instant failedDateTime;
        private String content;
        private Set<String> labels;

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

        public SearchFailedMessageResponseBuilder withDestination(Optional<String> destination) {
            this.destination = destination;
            return this;
        }

        public SearchFailedMessageResponseBuilder withSentDateTime(Instant sentDateTime) {
            this.sentDateTime = sentDateTime;
            return this;
        }

        public SearchFailedMessageResponseBuilder withFailedDateTime(Instant failedDateTime) {
            this.failedDateTime = failedDateTime;
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
            return new SearchFailedMessageResponse(failedMessageId, jmsMessageId, broker, destination, sentDateTime, failedDateTime, content, labels);
        }
    }
}
