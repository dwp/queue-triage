package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;

import static java.util.Objects.requireNonNull;

public class DelegatingMessageClassifier implements MessageClassifier {

    @JsonProperty
    private final FailedMessagePredicate predicate;
    @JsonProperty
    private final MessageClassifier messageClassifier;

    public DelegatingMessageClassifier(@JsonProperty("predicate") FailedMessagePredicate predicate,
                                       @JsonProperty("messageClassifier") MessageClassifier messageClassifier) {
        this.predicate = requireNonNull(predicate);
        this.messageClassifier = requireNonNull(messageClassifier);
    }

    @Override
    public MessageClassificationOutcome classify(MessageClassificationContext context) {
        final boolean matched = predicate.test(context.getFailedMessage());
        if (matched) {
            return context.matched(predicate).and(messageClassifier.classify(context));
        } else {
            return context.notMatched(predicate);
        }
    }

    @Override
    public String toString() {
        return "if " + predicate + " then " + messageClassifier;
    }
}
