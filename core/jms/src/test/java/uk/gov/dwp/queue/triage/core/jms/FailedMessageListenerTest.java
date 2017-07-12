package uk.gov.dwp.queue.triage.core.jms;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class FailedMessageListenerTest {

    private final FailedMessageFactory failedMessageFactory = mock(FailedMessageFactory.class);
    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);

    private final Message message = mock(Message.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    private final FailedMessageListener underTest = new FailedMessageListener(failedMessageFactory, failedMessageService);

    @Test
    public void processMessageSuccessfully() throws Exception {
        when(failedMessageFactory.createFailedMessage(message)).thenReturn(failedMessage);

        underTest.onMessage(message);

        verify(failedMessageService).create(failedMessage);
    }

    @Test
    public void exceptionIsThrownRetrievingTheJMSMessageId() throws JMSException {
        when(message.getJMSMessageID()).thenThrow(JMSException.class);

        underTest.onMessage(message);

        verifyZeroInteractions(failedMessageService);
    }
}