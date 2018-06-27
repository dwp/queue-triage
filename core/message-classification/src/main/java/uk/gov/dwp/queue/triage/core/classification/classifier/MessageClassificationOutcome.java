package uk.gov.dwp.queue.triage.core.classification.classifier;

import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static java.util.Objects.requireNonNull;

public class MessageClassificationOutcome<T> {

    private final boolean matched;
    private final Description<T> description;
    private final FailedMessage failedMessage;
    private final FailedMessageAction failedMessageAction;

    public static <T> MessageClassificationOutcome<T> matched(FailedMessage failedMessage, Description<T> description, FailedMessageAction failedMessageAction) {
        return new MessageClassificationOutcome<>(true, description, requireNonNull(failedMessage), requireNonNull(failedMessageAction));
    }

    public static <T> MessageClassificationOutcome<T> notMatched(FailedMessage failedMessage, Description<T> description) {
        return new MessageClassificationOutcome<>(false, description, failedMessage, null);
    }

    private MessageClassificationOutcome(boolean matched,
                                         Description<T> description,
                                         FailedMessage failedMessage,
                                         FailedMessageAction failedMessageAction) {
        this.matched = matched;
        this.description = description;
        this.failedMessage = failedMessage;
        this.failedMessageAction = failedMessageAction;
    }

    public boolean isMatched() {
        return matched;
    }

    FailedMessageAction getFailedMessageAction() {
        return failedMessageAction;
    }

    public Description<T> getDescription() {
        return description;
    }

    public FailedMessage getFailedMessage() {
        return failedMessage;
    }

    public MessageClassificationOutcome<T> append(String description) {
        return new MessageClassificationOutcome<>(matched, this.description.append(description), failedMessage, failedMessageAction);
    }

    public void execute() {
        if (failedMessageAction != null) {
            failedMessageAction.accept(failedMessage);
        }
    }

    @Override
    public String toString() {
        return "matched = " + matched + " " + description;
    }



}
