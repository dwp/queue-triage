package uk.gov.dwp.queue.triage.core.jms;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FailedMessageDataProviderTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final TextMessage textMessage = mock(TextMessage.class);

    private final FailedMessageDataProvider underTest = new FailedMessageDataProvider();

    @Test
    public void processingContinuesObjectPropertyCannotBeWritten() throws JMSException {
        when(failedMessage.getContent()).thenReturn("content");
        when(failedMessage.getProperties()).thenReturn(ImmutableMap.of("foo", "bar", "ham", "eggs"));
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);
        doThrow(new JMSException("Arrrghhhhh")).when(textMessage).setObjectProperty("foo", "bar");

        underTest.provide(textMessage, failedMessage);

        verify(textMessage).setText("content");
        verify(textMessage).setObjectProperty("foo", "bar");
        verify(textMessage).setObjectProperty("ham", "eggs");
        verify(textMessage).setStringProperty(FailedMessageId.FAILED_MESSAGE_ID, FAILED_MESSAGE_ID.toString());
    }
}