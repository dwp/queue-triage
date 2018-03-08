package uk.gov.dwp.queue.triage.core.jms.activemq;

import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.DestinationExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessagePropertyExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessageTextExtractor;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.jms.JMSException;
import javax.jms.Message;
import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import static java.time.Instant.now;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.DestinationMatcher.aDestination;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.FAILED_MESSAGE_ID;

public class ActiveMQFailedMessageFactoryTest {

    private static final Instant NOW = now();
    private static final String JMS_MESSAGE_ID = "ID:localhost.localdomain-46765-1518703251379-5:1:1:1:1";
    private static final FailedMessageId FAILED_MESSAGE_ID_VALUE = FailedMessageId.newFailedMessageId();
    private static final String MESSAGE_CONTENT = "Some text";

    @Rule
    public ExpectedException expectedException = none();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private MessageTextExtractor messageTextExtractor;
    @Mock
    private DestinationExtractor<ActiveMQMessage> destinationExtractor;
    @Mock
    private MessagePropertyExtractor messagePropertyExtractor;
    @Mock
    private ActiveMQMessage message;
    
    private ActiveMQFailedMessageFactory underTest;

    @Before
    public void setUp() {
        underTest = new ActiveMQFailedMessageFactory(messageTextExtractor, destinationExtractor, messagePropertyExtractor);
    }
    
    @Test
    public void exceptionIsThrownIfMessageIsNull() throws JMSException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Message cannot be null");
        underTest.createFailedMessage(null);
    }

    @Test
    public void exceptionIsThrownIfMessageIsNotActiveMQMessage() throws JMSException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Expected ActiveMQMessage received: " + SomeMessage.class.getName());

        underTest.createFailedMessage(new SomeMessage());
    }


    @Test
    public void createAFailedMessage() throws JMSException {

        when(messageTextExtractor.extractText(message)).thenReturn(MESSAGE_CONTENT);
        when(destinationExtractor.extractDestination(message)).thenReturn(new Destination("broker.name", of("queue.name")));
        when(messagePropertyExtractor.extractProperties(message)).thenReturn(emptyMap());
        when(message.getJMSMessageID()).thenReturn(JMS_MESSAGE_ID);
        when(message.getTimestamp()).thenReturn(NOW.minusSeconds(5).toEpochMilli());
        when(message.getBrokerInTime()).thenReturn(NOW.toEpochMilli());

        FailedMessage failedMessage = underTest.createFailedMessage(message);

        assertThat(failedMessage, is(aFailedMessage()
                .withJmsMessageId(equalTo(JMS_MESSAGE_ID))
                .withContent(equalTo(MESSAGE_CONTENT))
                .withDestination(aDestination().withBrokerName("broker.name").withName("queue.name"))
                .withSentAt(equalTo(NOW.minusSeconds(5)))
                .withFailedAt(equalTo(NOW))
                .withProperties(equalTo(emptyMap()))));
    }

    @Test
    public void createAFailedMessageWithFailedMessageIdFromProperty() throws JMSException {
        final Map<String, Object> messageProperties = singletonMap(FAILED_MESSAGE_ID, FAILED_MESSAGE_ID_VALUE.toString());
        when(messageTextExtractor.extractText(message)).thenReturn(MESSAGE_CONTENT);
        when(destinationExtractor.extractDestination(message)).thenReturn(new Destination("broker.name", of("queue.name")));
        when(messagePropertyExtractor.extractProperties(message)).thenReturn(messageProperties);
        when(message.getJMSMessageID()).thenReturn(JMS_MESSAGE_ID);
        when(message.getTimestamp()).thenReturn(NOW.minusSeconds(5).toEpochMilli());
        when(message.getBrokerInTime()).thenReturn(NOW.toEpochMilli());

        FailedMessage failedMessage = underTest.createFailedMessage(message);

        assertThat(failedMessage, is(aFailedMessage()
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID_VALUE))
                .withJmsMessageId(equalTo(JMS_MESSAGE_ID))
                .withContent(equalTo(MESSAGE_CONTENT))
                .withDestination(aDestination().withBrokerName("broker.name").withName("queue.name"))
                .withSentAt(equalTo(NOW.minusSeconds(5)))
                .withFailedAt(equalTo(NOW))
                .withProperties(equalTo(messageProperties))));
    }

    static class SomeMessage implements Message {

        @Override
        public String getJMSMessageID() {
            return null;
        }

        @Override
        public void setJMSMessageID(String id) {

        }

        @Override
        public long getJMSTimestamp() {
            return 0;
        }

        @Override
        public void setJMSTimestamp(long timestamp) {

        }

        @Override
        public byte[] getJMSCorrelationIDAsBytes() {
            return new byte[0];
        }

        @Override
        public void setJMSCorrelationIDAsBytes(byte[] correlationID) {

        }

        @Override
        public void setJMSCorrelationID(String correlationID) {

        }

        @Override
        public String getJMSCorrelationID() {
            return null;
        }

        @Override
        public javax.jms.Destination getJMSReplyTo() {
            return null;
        }

        @Override
        public void setJMSReplyTo(javax.jms.Destination replyTo) {

        }

        @Override
        public javax.jms.Destination getJMSDestination() {
            return null;
        }

        @Override
        public void setJMSDestination(javax.jms.Destination destination) {

        }

        @Override
        public int getJMSDeliveryMode() {
            return 0;
        }

        @Override
        public void setJMSDeliveryMode(int deliveryMode) {

        }

        @Override
        public boolean getJMSRedelivered() {
            return false;
        }

        @Override
        public void setJMSRedelivered(boolean redelivered) {

        }

        @Override
        public String getJMSType() {
            return null;
        }

        @Override
        public void setJMSType(String type) {

        }

        @Override
        public long getJMSExpiration() {
            return 0;
        }

        @Override
        public void setJMSExpiration(long expiration) {

        }

        @Override
        public int getJMSPriority() {
            return 0;
        }

        @Override
        public void setJMSPriority(int priority) {

        }

        @Override
        public void clearProperties() {

        }

        @Override
        public boolean propertyExists(String name) {
            return false;
        }

        @Override
        public boolean getBooleanProperty(String name) {
            return false;
        }

        @Override
        public byte getByteProperty(String name) {
            return 0;
        }

        @Override
        public short getShortProperty(String name) {
            return 0;
        }

        @Override
        public int getIntProperty(String name) {
            return 0;
        }

        @Override
        public long getLongProperty(String name) {
            return 0;
        }

        @Override
        public float getFloatProperty(String name) {
            return 0;
        }

        @Override
        public double getDoubleProperty(String name) {
            return 0;
        }

        @Override
        public String getStringProperty(String name) {
            return null;
        }

        @Override
        public Object getObjectProperty(String name) {
            return null;
        }

        @Override
        public Enumeration getPropertyNames() {
            return null;
        }

        @Override
        public void setBooleanProperty(String name, boolean value) {

        }

        @Override
        public void setByteProperty(String name, byte value) {

        }

        @Override
        public void setShortProperty(String name, short value) {

        }

        @Override
        public void setIntProperty(String name, int value) {

        }

        @Override
        public void setLongProperty(String name, long value) {

        }

        @Override
        public void setFloatProperty(String name, float value) {

        }

        @Override
        public void setDoubleProperty(String name, double value) {

        }

        @Override
        public void setStringProperty(String name, String value) {

        }

        @Override
        public void setObjectProperty(String name, Object value) {

        }

        @Override
        public void acknowledge() {

        }

        @Override
        public void clearBody() {

        }
    }
}