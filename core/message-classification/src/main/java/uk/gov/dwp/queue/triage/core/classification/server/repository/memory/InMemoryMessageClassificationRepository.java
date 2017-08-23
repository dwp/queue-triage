package uk.gov.dwp.queue.triage.core.classification.server.repository.memory;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMessageClassificationRepository implements MessageClassificationRepository {

    private final List<MessageClassifier> messageClassifiers;

    public InMemoryMessageClassificationRepository() {
        this(new ArrayList<>());
    }

    InMemoryMessageClassificationRepository(List<MessageClassifier> messageClassifiers) {
        this.messageClassifiers = messageClassifiers;
    }

    @Override
    public void insert(MessageClassifier messageClassifier) {
        messageClassifiers.add(messageClassifier);
    }

    @Override
    public List<MessageClassifier> findAll() {
        return new ArrayList<>(messageClassifiers);
    }

    @Override
    public void deleteAll() {
        messageClassifiers.clear();
    }
}
