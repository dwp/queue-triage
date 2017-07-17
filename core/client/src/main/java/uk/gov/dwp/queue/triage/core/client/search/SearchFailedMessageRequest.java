package uk.gov.dwp.queue.triage.core.client.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SearchFailedMessageRequest {

    private final String broker;
    private final Optional<String> destination;

    private SearchFailedMessageRequest(@JsonProperty("broker") String broker,
                                       @JsonProperty("destination") Optional<String> destination) {
        this.broker = broker;
        this.destination = destination;
    }

    public String getBroker() {
        return broker;
    }

    public Optional<String> getDestination() {
        return destination;
    }

    public static SearchFailedMessageRequestBuilder newSearchFailedMessageRequest() {
        return new SearchFailedMessageRequestBuilder();
    }

    public static class SearchFailedMessageRequestBuilder {

        private String broker;
        private Optional<String> destination = Optional.empty();

        private SearchFailedMessageRequestBuilder() {}

        public SearchFailedMessageRequestBuilder withBroker(String broker) {
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

        public SearchFailedMessageRequest build() {
            return new SearchFailedMessageRequest(broker, destination);
        }
    }
}
