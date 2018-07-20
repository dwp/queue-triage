package uk.gov.dwp.queue.triage.core.classification.classifier;

import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Objects;

public class MessageClassificationContext {

    public final FailedMessage failedMessage;

    public MessageClassificationContext(FailedMessage failedMessage) {
        this.failedMessage = failedMessage;
    }

    public FailedMessage getFailedMessage() {
        return failedMessage;
    }

    public MessageClassificationOutcome matched(FailedMessagePredicate predicate) {
        return matched(predicate, null);
    }

    public MessageClassificationOutcome matched(FailedMessagePredicate predicate, FailedMessageAction action) {
        return new MessageClassificationOutcome(true, new FailedMessagePredicateWithResult(true, predicate), failedMessage, action);
    }

    public MessageClassificationOutcome notMatched(FailedMessagePredicate predicate) {
        return new MessageClassificationOutcome(false, new FailedMessagePredicateWithResult(false, predicate), failedMessage,null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageClassificationContext that = (MessageClassificationContext) o;
        return Objects.equals(failedMessage, that.failedMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(failedMessage);
    }
}
