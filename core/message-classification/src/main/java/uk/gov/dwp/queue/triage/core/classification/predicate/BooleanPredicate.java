package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

@JsonTypeName("boolean")
public class BooleanPredicate implements FailedMessagePredicate {

    @JsonProperty
    private final boolean result;

    public BooleanPredicate(@JsonProperty("result") boolean result) {
        this.result = result;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        return object instanceof BooleanPredicate;
    }

    @Override
    public Description describe(Description description) {
        return description.append(result);
    }
}
