package uk.gov.dwp.queue.triage.core.client.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SearchFailedMessageRequest {

    private final Optional<String> broker;
    private final Optional<String> destination;
    private final Set<FailedMessageStatus> statuses;

    private SearchFailedMessageRequest(@JsonProperty("broker") Optional<String> broker,
                                       @JsonProperty("destination") Optional<String> destination,
                                       @JsonProperty("statuses") Set<FailedMessageStatus> statuses) {
        this.broker = broker;
        this.destination = destination;
        this.statuses = statuses;
    }

    public Optional<String> getBroker() {
        return broker;
    }

    public Optional<String> getDestination() {
        return destination;
    }

    public Set<FailedMessageStatus> getStatuses() {
        return statuses;
    }

    public static SearchFailedMessageRequestBuilder newSearchFailedMessageRequest() {
        return new SearchFailedMessageRequestBuilder()
                .withStatus(FailedMessageStatus.FAILED);
    }

    public static class SearchFailedMessageRequestBuilder {

        private Optional<String> broker = Optional.empty();
        private Optional<String> destination = Optional.empty();
        private Set<FailedMessageStatus> statuses = new HashSet<>();

        private SearchFailedMessageRequestBuilder() {}

        public SearchFailedMessageRequestBuilder withBroker(String broker) {
            this.broker = Optional.ofNullable(broker);
            return this;
        }

        public SearchFailedMessageRequestBuilder withBroker(Optional<String> broker) {
            this.broker = broker;
            return this;
        }

        public SearchFailedMessageRequestBuilder withDestination(String destination) {
            this.destination = Optional.ofNullable(destination);
            return this;
        }

        public SearchFailedMessageRequestBuilder withDestination(Optional<String> destination) {
            this.destination = destination;
            return this;
        }

        public SearchFailedMessageRequestBuilder withStatuses(Set<FailedMessageStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public SearchFailedMessageRequestBuilder withStatus(FailedMessageStatus status) {
            this.statuses.add(status);
            return this;
        }

        public SearchFailedMessageRequest build() {
            return new SearchFailedMessageRequest(broker, destination, statuses);
        }
    }
}
