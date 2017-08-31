package uk.gov.dwp.queue.triage.core.jms.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessageCreator;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Map;

public class FailedMessageCreator implements MessageCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageCreator.class);

    private final FailedMessage failedMessage;

    public FailedMessageCreator(FailedMessage failedMessage) {
        this.failedMessage = failedMessage;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(failedMessage.getContent());
        Map<String, Object> properties = failedMessage.getProperties();
        properties.keySet().forEach(key -> {
            try {
                textMessage.setObjectProperty(key, properties.get(key));
            } catch (JMSException ignore) {
                // TODO: Should we just ignore the fact we couldn't set a property on the message?
                LOGGER.warn("Could not add property: '{}' when sending FailedMessage: {}", key, failedMessage.getFailedMessageId());
            }
        });
        return textMessage;
    }

    public interface FailedMessageCreatorFactory {
        FailedMessageCreator create(FailedMessage failedMessage);
    }
}
