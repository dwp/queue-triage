package uk.gov.dwp.queue.triage.core.classification.server.resource;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationClient;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;

import java.util.List;

public class MessageClassificationResource implements MessageClassificationClient {

    private final MessageClassificationRepository messageClassificationRepository;

    public MessageClassificationResource(MessageClassificationRepository messageClassificationRepository) {
        this.messageClassificationRepository = messageClassificationRepository;
    }

    @Override
    public void addMessageClassifier(MessageClassifier messageClassifier) {
        messageClassificationRepository.insert(messageClassifier);
    }

    @Override
    public List<MessageClassifier> listAllMessageClassifiers() {
        return messageClassificationRepository.findAll();
    }

    @Override
    public void removeAllMessageClassifiers() {
        messageClassificationRepository.deleteAll();
    }
}
