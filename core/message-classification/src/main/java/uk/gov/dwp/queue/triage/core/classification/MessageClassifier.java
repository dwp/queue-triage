package uk.gov.dwp.queue.triage.core.classification;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MessageClassifier implements Predicate<FailedMessage>, Consumer<FailedMessage> {

    @JsonProperty
    private final List<FailedMessagePredicate> predicates;
    @JsonProperty
    private final FailedMessageAction action;

    private MessageClassifier(@JsonProperty("predicates") List<FailedMessagePredicate> predicates,
                              @JsonProperty("action") FailedMessageAction action) {
        this.predicates = predicates;
        this.action = action;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return predicates
                .stream()
                .allMatch(x -> x.test(failedMessage));
    }

    @Override
    public void accept(FailedMessage failedMessage) {
        if (test(failedMessage)) {
            action.accept(failedMessage);
        }
    }

    public static MessageClassifierBuilder when(FailedMessagePredicate failedMessagePredicate) {
        return new MessageClassifierBuilder(failedMessagePredicate);
    }

    public static class MessageClassifierBuilder {

        private final List<FailedMessagePredicate> predicates = new ArrayList<>();

        private MessageClassifierBuilder(FailedMessagePredicate failedMessagePredicate) {
            this.predicates.add(failedMessagePredicate);
        }

        public MessageClassifierBuilder and(FailedMessagePredicate failedMessagePredicate) {
            predicates.add(failedMessagePredicate);
            return this;
        }

        public MessageClassifier then(FailedMessageAction action) {
            return new MessageClassifier(predicates, action);
        }
    }
}
