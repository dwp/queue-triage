package uk.gov.dwp.queue.triage.core.resend;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.SENT;

public class FailedMessageResenderTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private final MessageSender messageSender = mock(MessageSender.class);
    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    private final FailedMessageResender underTest = new FailedMessageResender(
            messageSender,
            failedMessageService
    );

    @Test
    public void successfullySendMessage() throws Exception {
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);

        underTest.send(failedMessage);

        verify(messageSender).send(failedMessage);
        verify(failedMessageService).updateStatus(FAILED_MESSAGE_ID, SENT);
    }
}