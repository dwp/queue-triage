package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.List;

public class OrPredicate implements FailedMessagePredicate {

    @JsonProperty
    private List<FailedMessagePredicate> predicates;

    public OrPredicate(@JsonProperty("predicates") List<FailedMessagePredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return predicates
                .stream()
                .anyMatch(p -> p.test(failedMessage));
    }

    List<FailedMessagePredicate> getPredicates() {
        return predicates;
    }
}
