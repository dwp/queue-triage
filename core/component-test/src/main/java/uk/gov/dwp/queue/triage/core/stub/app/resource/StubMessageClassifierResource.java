package uk.gov.dwp.queue.triage.core.stub.app.resource;

import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.newClassifierCollection;

public class StubMessageClassifierResource {

    private final MessageClassificationRepository messageClassificationRepository;
    private final MessageClassifier defaultMessageClassifier;

    public StubMessageClassifierResource(MessageClassificationRepository messageClassificationRepository,
                                         MessageClassifier defaultMessageClassifier) {
        this.messageClassificationRepository = messageClassificationRepository;
        this.defaultMessageClassifier = defaultMessageClassifier;
    }

    public void addMessageClassifier(MessageClassifier messageClassifier) {
        final MessageClassifier currentClassifier = messageClassificationRepository.findLatest();

        final MessageClassifierGroup.Builder builder = newClassifierCollection();

        if (currentClassifier instanceof MessageClassifierGroup) {
            final List<MessageClassifier> classifiers = new ArrayList<>(((MessageClassifierGroup)currentClassifier).getClassifiers());
            classifiers.remove(defaultMessageClassifier);
            builder.withMessageClassifiers(classifiers);  // Add existing
        }

        messageClassificationRepository.save(builder
                .withClassifier(messageClassifier)        // Add new
                .withClassifier(defaultMessageClassifier) // Add default
                .build());
    }

    public void clear() {
        this.messageClassificationRepository.deleteAll();
    }
}
