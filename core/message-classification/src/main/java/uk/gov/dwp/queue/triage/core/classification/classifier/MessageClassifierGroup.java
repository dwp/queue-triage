package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    public <T> MessageClassificationOutcome<T> classify(FailedMessage failedMessage, Description<T> description) {
        appendParenthesisIfRequired("( ", description);
        final Iterator<MessageClassifier> it = messageClassifiers.iterator();
        while (it.hasNext()) {
            final MessageClassificationOutcome<T> outcome = it.next().classify(failedMessage, description.append("( "));
            description.append(" )");
            if (outcome.isMatched()) {
                appendParenthesisIfRequired(" )", description);
                return outcome;
            } else if (it.hasNext()) {
                description.append(" OR ");
            }
        }
        appendParenthesisIfRequired(" )", description);
        return MessageClassificationOutcome.notMatched(failedMessage, description);
    }

    private <T> void appendParenthesisIfRequired(String parenthesis, Description<T> description) {
        if (messageClassifiers.size() > 1) {
            description.append(parenthesis);
        }
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
