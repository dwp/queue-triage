package uk.gov.dwp.queue.triage.core.jms.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.jms.spring.FailedMessageCreator.FailedMessageCreatorFactory;

public class SpringMessageSender implements MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMessageSender.class);

    private final FailedMessageCreatorFactory failedMessageCreatorFactory;
    private final JmsTemplate jmsTemplate;

    public SpringMessageSender(JmsTemplate jmsTemplate) {
        this(FailedMessageCreator::new, jmsTemplate);
    }

    public SpringMessageSender(FailedMessageCreatorFactory failedMessageCreatorFactory,
                               JmsTemplate jmsTemplate) {
        this.failedMessageCreatorFactory = failedMessageCreatorFactory;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void send(FailedMessage failedMessage) {
        LOGGER.debug("Resending FailedMessage: {}", failedMessage.getFailedMessageId());
        jmsTemplate.send(failedMessageCreatorFactory.create(failedMessage));
    }
}
