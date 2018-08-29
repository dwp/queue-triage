package uk.gov.dwp.queue.triage.core.classification.classifier;

import uk.gov.dwp.queue.triage.core.classification.predicate.BooleanPredicate;

/**
 * A simple {@link MessageClassifier} that always returns an unmatched {@link MessageClassificationOutcome}}
 */
public final class UnmatchedMessageClassifier implements MessageClassifier {

    public static final UnmatchedMessageClassifier ALWAYS_UNMATCHED = new UnmatchedMessageClassifier();

    private UnmatchedMessageClassifier() {
        // Do Nothing
    }

    @Override
    public MessageClassificationOutcome classify(MessageClassificationContext context) {
        return context.notMatched(new BooleanPredicate(false));
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
