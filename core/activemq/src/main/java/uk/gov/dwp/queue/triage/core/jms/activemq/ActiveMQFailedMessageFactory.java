package uk.gov.dwp.queue.triage.core.jms.activemq;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.jms.DestinationExtractor;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageFactory;
import uk.gov.dwp.queue.triage.core.jms.MessagePropertyExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessageTextExtractor;

import javax.jms.JMSException;
import javax.jms.Message;
import java.time.Instant;

public class ActiveMQFailedMessageFactory implements FailedMessageFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQFailedMessageFactory.class);

    private final MessageTextExtractor messageTextExtractor;
    private final DestinationExtractor<ActiveMQMessage> destinationExtractor;
    private final MessagePropertyExtractor messagePropertyExtractor;

    public ActiveMQFailedMessageFactory(MessageTextExtractor messageTextExtractor,
                                        DestinationExtractor<ActiveMQMessage> destinationExtractor,
                                        MessagePropertyExtractor messagePropertyExtractor) {
        this.messageTextExtractor = messageTextExtractor;
        this.destinationExtractor = destinationExtractor;
        this.messagePropertyExtractor = messagePropertyExtractor;
    }

    @Override
    public FailedMessage createFailedMessage(Message message) throws JMSException {
        validateMessageOfCorrectType(message);
        ActiveMQMessage activeMQMessage = (ActiveMQMessage) message;
        return FailedMessageBuilder.newFailedMessage()
                .withJmsMessageId(message.getJMSMessageID())
                .withContent(messageTextExtractor.extractText(message))
                .withDestination(destinationExtractor.extractDestination(activeMQMessage))
                .withSentDateTime(extractTimestamp(activeMQMessage.getTimestamp()))
                .withFailedDateTime(extractTimestamp(activeMQMessage.getBrokerInTime()))
                .withProperties(messagePropertyExtractor.extractProperties(message))
                .withFailedMessageIdFromPropertyIfPresent()
                .build();
    }

    private void validateMessageOfCorrectType(Message in) {
        if (!(in instanceof ActiveMQMessage)) {
            String errorMessage;
            if (in == null) {
                errorMessage = "Message cannot be null";
            } else {
                errorMessage = "Expected ActiveMQMessage received: " + in.getClass().getName();
            }
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private Instant extractTimestamp(long ms) {
        return (ms != 0) ? Instant.ofEpochMilli(ms) : null;
    }
}