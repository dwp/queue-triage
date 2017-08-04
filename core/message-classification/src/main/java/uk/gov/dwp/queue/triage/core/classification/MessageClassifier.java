package uk.gov.dwp.queue.triage.core.classification;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public class MessageClassifier {

    private final FailedMessagePredicate predicate;
    private final FailedMessageAction action;

    public MessageClassifier(@JsonProperty("predicate") FailedMessagePredicate predicate,
                             @JsonProperty("action") FailedMessageAction action) {
        this.predicate = predicate;
        this.action = action;
    }

    public boolean classify(FailedMessage failedMessage) {
        if (predicate.test(failedMessage)) {
            action.accept(failedMessage);
            return true;
        }
        return false;
    }
}
