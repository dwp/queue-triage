package uk.gov.dwp.queue.triage.core.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.service.processor.FailedMessageProcessor;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class FailedMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageListener.class);

    private final FailedMessageFactory failedMessageFactory;
    private final FailedMessageProcessor failedMessageProcessor;

    public FailedMessageListener(FailedMessageFactory failedMessageFactory,
                                 FailedMessageProcessor failedMessageProcessor) {
        this.failedMessageFactory = failedMessageFactory;
        this.failedMessageProcessor = failedMessageProcessor;
    }

    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.debug("Received message: {} with CorrelationId: {}", message.getJMSMessageID(), message.getJMSCorrelationID());
            failedMessageProcessor.process(failedMessageFactory.createFailedMessage(message));
        } catch (JMSException e) {
            LOGGER.error("Could not read jmsMessageId or correlationId", e);
        }
    }
}
