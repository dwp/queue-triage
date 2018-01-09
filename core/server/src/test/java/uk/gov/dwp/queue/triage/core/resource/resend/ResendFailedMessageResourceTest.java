package uk.gov.dwp.queue.triage.core.resource.resend;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEventMatcher;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.RESEND;

public class ResendFailedMessageResourceTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();
    private static final Clock CLOCK = Clock.fixed(NOW, UTC);

    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final ResendFailedMessageResource underTest = new ResendFailedMessageResource(failedMessageService, CLOCK);

    @Test
    public void successfullyMarkAMessageForResending() throws Exception {
        underTest.resendFailedMessage(FAILED_MESSAGE_ID);

        verify(failedMessageService).updateStatus(FAILED_MESSAGE_ID, RESEND);
    }

    @Test
    public void successfullyMarkAMessageForResendingInTheFuture() throws Exception {
        underTest.resendFailedMessageWithDelay(FAILED_MESSAGE_ID, Duration.ofSeconds(100));

        verify(failedMessageService).updateStatus(
                eq(FAILED_MESSAGE_ID),
                argThat(new HamcrestArgumentMatcher<>(StatusHistoryEventMatcher.equalTo(RESEND)
                        .withUpdatedDateTime(Matchers.equalTo(NOW.plus(100,  SECONDS))))));
    }
}