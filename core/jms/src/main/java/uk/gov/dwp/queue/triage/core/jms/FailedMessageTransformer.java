package uk.gov.dwp.queue.triage.core.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Map;

public class FailedMessageTransformer implements JmsMessageTransformer<TextMessage, FailedMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageTransformer.class);
    @Override
    public void transform(TextMessage textMessage, FailedMessage data) throws JMSException {
        textMessage.setText(data.getContent());
        Map<String, Object> properties = data.getProperties();
        properties.keySet().forEach(key -> {
            try {
                textMessage.setObjectProperty(key, properties.get(key));
            } catch (JMSException ignore) {
                LOGGER.warn("Could not add property: '{}' when sending FailedMessage: {}", key, data.getFailedMessageId());
            }
        });
        textMessage.setStringProperty(FailedMessageId.FAILED_MESSAGE_ID, data.getFailedMessageId().toString());
    }
}
