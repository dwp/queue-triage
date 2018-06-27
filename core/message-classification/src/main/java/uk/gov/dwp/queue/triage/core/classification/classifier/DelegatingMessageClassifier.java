package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

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
    public <T> MessageClassificationOutcome<T> classify(FailedMessage failedMessage, Description<T> description) {
        final boolean matched = predicate.test(failedMessage);
        predicate.describe(description).append(" [").append(matched).append("]");
        if (matched) {
            description.append(" AND ");
            return messageClassifier.classify(failedMessage, description);
        } else {
            return MessageClassificationOutcome.notMatched(failedMessage, description);
        }
    }

    @Override
    public String toString() {
        return "when " + predicate + " then " + messageClassifier;
    }
}
