package uk.gov.dwp.queue.triage.core.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class MessageTextExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTextExtractor.class);

    public String extractText(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                return textMessage.getText();
            } catch (JMSException e) {
                String errorMsg = String.format("Failed to extract the text from TextMessage: %s", textMessage.toString());
                LOGGER.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } else {
            String errorMsg = String.format("Expected TextMessage received: %s", message.getClass().getName());
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}
