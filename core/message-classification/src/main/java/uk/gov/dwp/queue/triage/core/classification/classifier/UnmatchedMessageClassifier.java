package uk.gov.dwp.queue.triage.core.classification.classifier;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcome.notMatched;

/**
 * A simple {@code MessageClassifier that always returns an unmatched {@code MessageClassificationOutcome}}
 */
public final class UnmatchedMessageClassifier implements MessageClassifier {

    public static final UnmatchedMessageClassifier ALWAYS_UNMATCHED = new UnmatchedMessageClassifier();

    private UnmatchedMessageClassifier() {
        // Do Nothing
    }

    @Override
    public <T> MessageClassificationOutcome<T> classify(FailedMessage failedMessage, Description<T> description) {
        description.append("always unmatched");
        return notMatched(failedMessage, description);
    }

    @Override
    public String toString() {
        return "unmatched";
    }

    @Override
    public int hashCode() {
        return 17 * 32;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
