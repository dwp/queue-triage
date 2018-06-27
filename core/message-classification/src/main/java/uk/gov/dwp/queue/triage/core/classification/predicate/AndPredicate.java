package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AndPredicate implements FailedMessagePredicate {

    @JsonProperty
    private List<FailedMessagePredicate> predicates;

    public AndPredicate(@JsonProperty("predicates") List<FailedMessagePredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return predicates
                .stream()
                .allMatch(p -> p.test(failedMessage));
    }

    List<FailedMessagePredicate> getPredicates() {
        return new ArrayList<>(predicates);
    }

    @Override
    public Description describe(Description description) {
        description.append("( ");
        final Iterator<FailedMessagePredicate> iterator = predicates.iterator();
        while (iterator.hasNext()) {
            iterator.next().describe(description);
            if (iterator.hasNext()) {
                description.append(" AND ");
            }
        }
        return description.append(" )");
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }
}
