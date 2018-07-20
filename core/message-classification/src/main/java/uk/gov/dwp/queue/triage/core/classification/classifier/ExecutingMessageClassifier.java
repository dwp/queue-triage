package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;

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
    public MessageClassificationOutcome classify(MessageClassificationContext context) {
        final boolean matched = predicate.test(context.getFailedMessage());
        if (matched) {
            return context.matched(predicate, action);
        } else {
            return context.notMatched(predicate);
        }
    }

    @Override
    public String toString() {
        return "if " + predicate + " then " + action;
    }
}
