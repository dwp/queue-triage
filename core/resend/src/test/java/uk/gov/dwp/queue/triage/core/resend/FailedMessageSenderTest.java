package uk.gov.dwp.queue.triage.core.resend;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.SENT;
import static uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequestMatcher.aStatusUpdateRequest;

public class FailedMessageSenderTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private final MessageSender messageSender = mock(MessageSender.class);
    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final ArgumentCaptor<StatusUpdateRequest> statusUpdateRequest = ArgumentCaptor.forClass(StatusUpdateRequest.class);

    private final FailedMessageSender underTest = new FailedMessageSender(
            messageSender,
            failedMessageService
    );

    @Test
    public void successfullySendMessage() {
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);

        underTest.send(failedMessage);

        verify(messageSender).send(failedMessage);
        verify(failedMessageService).update(eq(FAILED_MESSAGE_ID), statusUpdateRequest.capture());
        assertThat(statusUpdateRequest.getValue(), is(aStatusUpdateRequest(SENT)));
    }
}