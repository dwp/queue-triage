package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public class ExecutingMessageClassifier implements MessageClassifier {

    @JsonProperty
    private final FailedMessagePredicate predicate;
    @JsonProperty
    private final FailedMessageAction action;

    public ExecutingMessageClassifier(@JsonProperty("predicate") FailedMessagePredicate predicate,
                                      @JsonProperty("action") FailedMessageAction action) {
        this.predicate = predicate;
        this.action = action;
    }

    @Override
    public <T> MessageClassificationOutcome<T> classify(FailedMessage failedMessage, Description<T> description) {
        final boolean matched = predicate.test(failedMessage);
        predicate.describe(description).append(" [").append(matched).append("]");
        if (matched) {
            return MessageClassificationOutcome.matched(failedMessage, description, action);
        } else {
            return MessageClassificationOutcome.notMatched(failedMessage, description);
        }
    }

    @Override
    public String toString() {
        return "when " + predicate + " then " + action;
    }
}
