package uk.gov.dwp.queue.triage.core.classification.server.repository.memory;

import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.classifier.UnmatchedMessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;

import java.util.Vector;

public class InMemoryMessageClassificationRepository implements MessageClassificationRepository {

    private Vector<MessageClassifier> messageClassifiers;

    public InMemoryMessageClassificationRepository() {
        this(new Vector<>());
    }

    InMemoryMessageClassificationRepository(Vector<MessageClassifier> messageClassifiers) {
        this.messageClassifiers = messageClassifiers;
    }

    @Override
    public void save(MessageClassifier messageClassifier) {
        this.messageClassifiers.add(messageClassifier);
    }

    @Override
    public MessageClassifier findLatest() {
        return messageClassifiers.isEmpty() ? null : messageClassifiers.lastElement();
    }

    @Override
    public void deleteAll() {
        messageClassifiers.add(UnmatchedMessageClassifier.ALWAYS_UNMATCHED);
    }
}
