package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.predicate.BooleanPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageClassifierGroup implements MessageClassifier {

    public static Builder newClassifierCollection() {
        return new Builder();
    }

    public static Builder copyClassifierCollection(MessageClassifierGroup messageClassifierGroup) {
        return new Builder().withMessageClassifiers(messageClassifierGroup.getClassifiers());
    }

    @JsonProperty("classifiers")
    private final List<MessageClassifier> messageClassifiers;

    private MessageClassifierGroup(@JsonProperty("classifiers") List<MessageClassifier> messageClassifiers) {
        this.messageClassifiers = messageClassifiers;
    }

    @Override
    public MessageClassificationOutcome classify(MessageClassificationContext context) {
        MessageClassificationOutcome outcome = null;
        for (MessageClassifier messageClassifier : messageClassifiers) {
            final MessageClassificationOutcome latestOutcome = messageClassifier.classify(context);
            outcome = Optional.ofNullable(outcome).map(currentOutcome -> currentOutcome.or(latestOutcome)).orElse(latestOutcome);
            if (outcome.isMatched()) {
                return outcome;
            }
        }
        return Optional.ofNullable(outcome).orElse(context.notMatched(new BooleanPredicate(false)));
    }

    @Override
    public String toString() {
        return messageClassifiers.stream().map(MessageClassifier::toString).collect(Collectors.joining(" OR "));
    }

    public List<MessageClassifier> getClassifiers() {
        return Collections.unmodifiableList(messageClassifiers);
    }

    public static class Builder {

        private final List<MessageClassifier> messageClassifiers = new ArrayList<>();

        private Builder() {
            // Do nothing
        }

        public Builder withClassifier(MessageClassifier messageClassifier) {
            this.messageClassifiers.add(messageClassifier);
            return this;
        }

        public Builder withMessageClassifiers(Collection<MessageClassifier> messageClassifiers) {
            this.messageClassifiers.addAll(messageClassifiers);
            return this;
        }

        public Builder clear() {
            this.messageClassifiers.clear();
            return this;
        }

        public MessageClassifierGroup build() {
            return new MessageClassifierGroup(new ArrayList<>(messageClassifiers));
        }
    }
}
