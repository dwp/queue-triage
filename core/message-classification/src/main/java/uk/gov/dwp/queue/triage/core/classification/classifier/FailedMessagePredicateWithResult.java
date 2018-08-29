package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Objects;

/**
 * This {@link FailedMessagePredicate} is not designed to be used within a {@link MessageClassifier}
 */
public class FailedMessagePredicateWithResult implements FailedMessagePredicate {

    @JsonProperty
    private final boolean result;
    @JsonProperty
    private final FailedMessagePredicate predicate;

    public FailedMessagePredicateWithResult(@JsonProperty("result") boolean result,
                                            @JsonProperty("predicate") FailedMessagePredicate predicate) {
        this.result = result;
        this.predicate = predicate;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return result;
    }

    @Override
    public <T> Description<T> describe(Description<T> description) {
        return predicate.describe(description).append(" [").append(result).append("]");
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).getOutput();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailedMessagePredicateWithResult that = (FailedMessagePredicateWithResult) o;
        return result == that.result && Objects.equals(predicate, that.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, predicate);
    }
}
