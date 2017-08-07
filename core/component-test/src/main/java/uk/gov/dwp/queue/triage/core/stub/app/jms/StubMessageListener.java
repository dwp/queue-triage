package uk.gov.dwp.queue.triage.core.stub.app.jms;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageFactory;
import uk.gov.dwp.queue.triage.core.stub.app.repository.MessageClassifierRepository;

import javax.jms.Message;
import javax.jms.MessageListener;

public class StubMessageListener implements MessageListener {

    private final String brokerName;
    private final FailedMessageFactory failedMessageFactory;
    private final MessageClassifierRepository messageClassifierRepository;
    private final MessageClassifier  defaultMessageClassifier;

    public StubMessageListener(String brokerName,
                               FailedMessageFactory failedMessageFactory,
                               MessageClassifierRepository messageClassifierRepository,
                               MessageClassifier defaultMessageClassifier) {
        this.brokerName = brokerName;
        this.failedMessageFactory = failedMessageFactory;
        this.messageClassifierRepository = messageClassifierRepository;
        this.defaultMessageClassifier = defaultMessageClassifier;
    }

    @Override
    public void onMessage(Message message) {
        FailedMessage failedMessage = failedMessageFactory.createFailedMessage(message);
        messageClassifierRepository
                .getClassifiers(brokerName)
                .stream()
                .filter(classifier -> classifier.test(failedMessage))
                .findFirst()
                .orElse(defaultMessageClassifier)
                .classify(failedMessage);
    }
}
