package uk.gov.dwp.queue.triage.core.stub.app.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcome;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class StubMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StubMessageListener.class);

    private final FailedMessageFactory failedMessageFactory;
    private final MessageClassificationRepository messageClassificationRepository;

    public StubMessageListener(FailedMessageFactory failedMessageFactory,
                               MessageClassificationRepository messageClassificationRepository) {
        this.failedMessageFactory = failedMessageFactory;
        this.messageClassificationRepository = messageClassificationRepository;
    }

    @Override
    public void onMessage(Message message) {
        try {
            FailedMessage failedMessage = failedMessageFactory.createFailedMessage(message);
            LOGGER.debug("Received failedMessage with content: {}", failedMessage.getContent());
            final MessageClassificationOutcome outcome = messageClassificationRepository
                    .findLatest()
                    .classify(failedMessage, new StringDescription());
            if (outcome.isMatched()) {
                outcome.execute();
            } else {
                LOGGER.warn("FailedMessage: {} not classified.  Have you configured a defaultClassifier for the stub?", failedMessage.getFailedMessageId());
            }
        } catch (JMSException ignore) {}
    }
}
