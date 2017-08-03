package uk.gov.dwp.queue.triage.core.resource.resend;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.RESEND;

public class ResendFailedMessageResourceTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final ResendFailedMessageResource underTest = new ResendFailedMessageResource(failedMessageService);

    @Test
    public void successfullyMarkAMessageForResending() throws Exception {
        underTest.resendFailedMessage(FAILED_MESSAGE_ID);

        verify(failedMessageService).updateStatus(FAILED_MESSAGE_ID, RESEND);
    }
}