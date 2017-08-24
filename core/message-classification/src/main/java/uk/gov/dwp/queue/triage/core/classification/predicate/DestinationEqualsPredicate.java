package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Objects;
import java.util.Optional;

public class DestinationEqualsPredicate implements FailedMessagePredicate {

    @JsonProperty
    private final Optional<String> destination;

    public DestinationEqualsPredicate(@JsonProperty("destination") Optional<String> destination) {
        this.destination = Objects.requireNonNull(destination);
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return destination.equals(failedMessage.getDestination().getName());
    }

    @Override
    public String toString() {
        return "destination equals: " + destination;
    }
}
