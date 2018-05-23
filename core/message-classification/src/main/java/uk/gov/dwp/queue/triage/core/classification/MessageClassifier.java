package uk.gov.dwp.queue.triage.core.classification;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class MessageClassifier implements Predicate<FailedMessage>, Consumer<FailedMessage> {

    @JsonProperty
    private final String description;
    @JsonProperty
    private final FailedMessagePredicate predicate;
    @JsonProperty
    private final FailedMessageAction action;

    public MessageClassifier(@JsonProperty("description") String description,
                             @JsonProperty("predicate") FailedMessagePredicate predicate,
                             @JsonProperty("action") FailedMessageAction action) {
        this.description = description;
        this.predicate = predicate;
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return predicate.test(failedMessage);
    }

    @Override
    public void accept(FailedMessage failedMessage) {
        if (test(failedMessage)) {
            action.accept(failedMessage);
        }
    }
}
