package uk.gov.dwp.queue.triage.core.classification.server.repository.memory;

import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.classifier.UnmatchedMessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMessageClassificationRepository implements MessageClassificationRepository {

    private List<MessageClassifier> messageClassifiers;

    public InMemoryMessageClassificationRepository() {
        this(new ArrayList<>());
    }

    InMemoryMessageClassificationRepository(List<MessageClassifier> messageClassifiers) {
        this.messageClassifiers = messageClassifiers;
    }

    @Override
    public void save(MessageClassifier messageClassifier) {
        this.messageClassifiers.add(messageClassifier);
    }

    @Override
    public MessageClassifier findLatest() {
        int lastElement = messageClassifiers.size() - 1;
        return messageClassifiers.get(lastElement);
    }

    @Override
    public void deleteAll() {
        messageClassifiers.add(UnmatchedMessageClassifier.ALWAYS_UNMATCHED);
    }
}
