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

    public List<FailedMessagePredicate> getPredicates() {
        return new ArrayList<>(predicates);
    }

    @Override
    public Description describe(Description description) {
        Description finalDescription = description.append("( ");
        final Iterator<FailedMessagePredicate> iterator = predicates.iterator();
        while (iterator.hasNext()) {
            finalDescription = iterator.next().describe(finalDescription);
            if (iterator.hasNext()) {
                finalDescription = finalDescription.append(" AND ");
            }
        }
        return finalDescription.append(" )");
    }

    public AndPredicate and(FailedMessagePredicate failedMessagePredicate) {
        List<FailedMessagePredicate> predicates = this.getPredicates();
        if (failedMessagePredicate instanceof AndPredicate) {
            predicates.addAll(((AndPredicate)failedMessagePredicate).getPredicates());
        } else {
            predicates.add(failedMessagePredicate);
        }
        return new AndPredicate(predicates);
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }

    public static AndPredicate and(FailedMessagePredicate lhs, FailedMessagePredicate rhs) {
        if (lhs instanceof AndPredicate) {
            return ((AndPredicate) lhs).and(rhs);
        }
        List<FailedMessagePredicate> predicates = new ArrayList<>();
        predicates.add(lhs);
        if (rhs instanceof AndPredicate) {
            predicates.addAll(((AndPredicate)rhs).getPredicates());
        } else {
            predicates.add(rhs);
        }
        return new AndPredicate(predicates);
    }
}
