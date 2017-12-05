package uk.gov.dwp.queue.triage.core.client.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.Operator.AND;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.Operator.OR;

public class SearchFailedMessageRequest {

    private final Optional<String> broker;
    private final Optional<String> destination;
    private final Set<FailedMessageStatus> statuses;
    private final Optional<String> content;
    private final Operator operator;

    public static SearchFailedMessageRequestBuilder searchMatchingAllCriteria() {
        return new SearchFailedMessageRequestBuilder(AND);
    }

    public static SearchFailedMessageRequestBuilder searchMatchingAnyCriteria() {
        return new SearchFailedMessageRequestBuilder(OR);
    }

    private SearchFailedMessageRequest(@JsonProperty("broker") Optional<String> broker,
                                       @JsonProperty("destination") Optional<String> destination,
                                       @JsonProperty("statuses") Set<FailedMessageStatus> statuses,
                                       @JsonProperty("content") Optional<String> content,
                                       @JsonProperty("operator") Operator operator) {
        this.broker = broker;
        this.destination = destination;
        this.statuses = statuses;
        this.content = content;
        this.operator = operator;
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

    public Optional<String> getContent() {
        return content;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public enum Operator {
        AND, OR
    }

    public static class SearchFailedMessageRequestBuilder {

        private Optional<String> broker = Optional.empty();
        private Optional<String> destination = Optional.empty();
        private Set<FailedMessageStatus> statuses = new HashSet<>();
        private Optional<String> content = Optional.empty();
        private Operator operator;

        private SearchFailedMessageRequestBuilder(Operator operator) {
            this.operator = operator;
        }

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

        public SearchFailedMessageRequestBuilder withContent(String content) {
            this.content = Optional.ofNullable(content);
            return this;
        }

        public SearchFailedMessageRequest build() {
            return new SearchFailedMessageRequest(broker, destination, statuses, content, operator);
        }
    }
}
