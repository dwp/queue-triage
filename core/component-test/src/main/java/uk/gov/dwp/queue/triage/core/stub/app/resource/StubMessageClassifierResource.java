package uk.gov.dwp.queue.triage.core.stub.app.resource;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.stub.app.repository.MessageClassifierRepository;

public class StubMessageClassifierResource {

    private final String brokerName;
    private final MessageClassifierRepository messageClassifierRepository;

    public StubMessageClassifierResource(String brokerName, MessageClassifierRepository messageClassifierRepository) {
        this.brokerName = brokerName;
        this.messageClassifierRepository = messageClassifierRepository;
    }

    public void addMessageClassifier(MessageClassifier messageClassifier) {
        this.messageClassifierRepository.addClassifier(brokerName, messageClassifier);
    }

    public void clear() {
        this.messageClassifierRepository.removeAll();
    }
}
