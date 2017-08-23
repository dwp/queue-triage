package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Objects;

public class BrokerEqualsPredicate implements FailedMessagePredicate {

    @JsonProperty
    private final String broker;

    public BrokerEqualsPredicate(@JsonProperty("broker") String broker) {
        this.broker = Objects.requireNonNull(broker);
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return broker.equals(failedMessage.getDestination().getBrokerName());
    }

    @Override
    public String toString() {
        return "broker equals: " + broker;
    }
}
