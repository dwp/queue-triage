package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Iterator;
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

    public List<FailedMessagePredicate> getPredicates() {
        return predicates;
    }

    @Override
    public <T> Description<T> describe(Description<T> description) {
        Description<T> finalDescription = description.append("( ");
        final Iterator<FailedMessagePredicate> iterator = predicates.iterator();
        while (iterator.hasNext()) {
            finalDescription = iterator.next().describe(finalDescription);
            if (iterator.hasNext()) {
                finalDescription = finalDescription.append(" OR ");
            }
        }
        return finalDescription.append(" )");
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }
}
