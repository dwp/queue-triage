package uk.gov.dwp.queue.triage.core.jms.spring;

import org.springframework.jms.core.MessageCreator;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageDataProvider;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Collections;
import java.util.List;

public class FailedMessageCreator implements MessageCreator {

    private final FailedMessage failedMessage;
    private final List<FailedMessageDataProvider> failedMessageDataProviders;

    public FailedMessageCreator(FailedMessage failedMessage) {
        this.failedMessage = failedMessage;
        this.failedMessageDataProviders = Collections.singletonList(new FailedMessageDataProvider());
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        TextMessage textMessage = session.createTextMessage();
        for (FailedMessageDataProvider failedMessageDataProvider : failedMessageDataProviders) {
            failedMessageDataProvider.provide(textMessage, failedMessage);
        }
        return textMessage;
    }

}
