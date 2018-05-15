package uk.gov.dwp.queue.triage.core.resource.resend;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.RESEND;
import static uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequestMatcher.aStatusUpdateRequest;

public class ResendFailedMessageResourceTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();
    private static final Clock CLOCK = Clock.fixed(NOW, UTC);

    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final ResendFailedMessageResource underTest = new ResendFailedMessageResource(failedMessageService, CLOCK);

    private final ArgumentCaptor<StatusUpdateRequest> statusUpdateRequest = ArgumentCaptor.forClass(StatusUpdateRequest.class);

    @Test
    public void successfullyMarkAMessageForResending() {
        underTest.resendFailedMessage(FAILED_MESSAGE_ID);

        verify(failedMessageService).update(eq(FAILED_MESSAGE_ID), statusUpdateRequest.capture());
        assertThat(statusUpdateRequest.getValue(), aStatusUpdateRequest(RESEND));
    }

    @Test
    public void successfullyMarkAMessageForResendingInTheFuture() {
        underTest.resendFailedMessageWithDelay(FAILED_MESSAGE_ID, Duration.ofSeconds(100));

        verify(failedMessageService).update(eq(FAILED_MESSAGE_ID), statusUpdateRequest.capture());
        assertThat(statusUpdateRequest.getValue(), aStatusUpdateRequest(RESEND)
                        .withUpdatedDateTime(Matchers.equalTo(NOW.plus(100,  SECONDS))));
    }
}