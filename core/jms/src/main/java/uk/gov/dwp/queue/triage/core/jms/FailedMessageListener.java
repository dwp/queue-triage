package uk.gov.dwp.queue.triage.core.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class FailedMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageListener.class);

    private final FailedMessageFactory failedMessageFactory;
    private final FailedMessageService failedMessageService;

    public FailedMessageListener(FailedMessageFactory failedMessageFactory,
                                 FailedMessageService failedMessageService) {
        this.failedMessageFactory = failedMessageFactory;
        this.failedMessageService = failedMessageService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.debug("Received message: {} with CorrelationId: {}", message.getJMSMessageID(), message.getJMSCorrelationID());
            // Some sort of strategy
            // When running in ReadOnly mode if JMS Message Id already exists ignore
            // When running in Write mode if FailedMessage with a duplicate Id already exists, i.e. it's dead-lettered again update rather than create
            failedMessageService.create(failedMessageFactory.createFailedMessage(message));
        } catch (JMSException e) {
            LOGGER.error("Could not read jmsMessageId or correlationId", e);
        }
    }
}
