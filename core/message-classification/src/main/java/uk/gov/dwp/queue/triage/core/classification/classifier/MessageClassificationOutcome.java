package uk.gov.dwp.queue.triage.core.classification.classifier;

import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.AndPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.OrPredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Arrays;
import java.util.Optional;

public class MessageClassificationOutcome {

    private final boolean matched;
    private final FailedMessagePredicate predicate;
    private final FailedMessage failedMessage;
    private final FailedMessageAction action;

    MessageClassificationOutcome(boolean matched,
                                 FailedMessagePredicate predicate,
                                 FailedMessage failedMessage,
                                 FailedMessageAction action) {
        this.matched = matched;
        this.predicate = predicate;
        this.failedMessage = failedMessage;
        this.action = action;
    }

    public boolean isMatched() {
        return matched;
    }

    public String getDescription() {
        return predicate.describe(new StringDescription().append("matched = ").append(matched).append(", ")).getOutput();
    }

    public <T> Description<T> getDescription(Description<T> description) {
        // TODO
        return description;
    }

    public FailedMessage getFailedMessage() {
        return failedMessage;
    }

    public void execute() {
        if (action != null) {
            action.accept(failedMessage);
        }
    }

    FailedMessageAction getFailedMessageAction() {
        return action;
    }

    FailedMessagePredicate getFailedMessagePredicate() {
        return predicate;
    }

    @Override
    public String toString() {
        return "matched = " + matched + " " + predicate;
    }

    public MessageClassificationOutcome and(MessageClassificationOutcome outcome) {
        final boolean matched = this.matched && outcome.isMatched();
        return new MessageClassificationOutcome(
                matched,
                AndPredicate.and(predicate, outcome.predicate),
                failedMessage,
                matched ? Optional.ofNullable(outcome.getFailedMessageAction()).orElse(action) : null
        );
    }

    public MessageClassificationOutcome or(MessageClassificationOutcome outcome) {
        final boolean matched = this.matched || outcome.isMatched();
        return new MessageClassificationOutcome(
                matched,
                new OrPredicate(Arrays.asList(predicate, outcome.predicate)),
                failedMessage,
                Optional.ofNullable(outcome.getFailedMessageAction()).orElse(action)
        );
    }
}
