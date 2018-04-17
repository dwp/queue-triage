package uk.gov.dwp.queue.triage.core.jms.spring;

import org.springframework.jms.core.MessageCreator;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageTransformer;
import uk.gov.dwp.queue.triage.core.jms.JmsMessageTransformer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Collections;
import java.util.List;

public class FailedMessageCreator implements MessageCreator {

    private final FailedMessage failedMessage;
    private final List<JmsMessageTransformer<TextMessage, FailedMessage>> jmsMessageTransformers;

    public FailedMessageCreator(FailedMessage failedMessage) {
        this(failedMessage, Collections.singletonList(new FailedMessageTransformer()));
    }

    public FailedMessageCreator(FailedMessage failedMessage, List<JmsMessageTransformer<TextMessage, FailedMessage>> jmsMessageTransformers) {
        this.failedMessage = failedMessage;
        this.jmsMessageTransformers = jmsMessageTransformers;

    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        TextMessage textMessage = session.createTextMessage();
        for (JmsMessageTransformer<TextMessage, FailedMessage> jmsMessageTransformer : jmsMessageTransformers) {
            jmsMessageTransformer.transform(textMessage, failedMessage);
        }
        return textMessage;
    }

}
