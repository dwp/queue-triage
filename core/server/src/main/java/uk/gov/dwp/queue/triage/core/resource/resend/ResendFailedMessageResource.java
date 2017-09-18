package uk.gov.dwp.queue.triage.core.resource.resend;

import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.RESEND;

public class ResendFailedMessageResource implements ResendFailedMessageClient {

    private final FailedMessageService failedMessageService;
    private final Clock clock;

    public ResendFailedMessageResource(FailedMessageService failedMessageService,
                                       Clock clock) {
        this.failedMessageService = failedMessageService;
        this.clock = clock;
    }

    @Override
    public void resendFailedMessage(FailedMessageId failedMessageId) {
        failedMessageService.updateStatus(failedMessageId, RESEND);
    }

    @Override
    public void resendFailedMessageWithDelay(FailedMessageId failedMessageId, Duration duration) {
        failedMessageService.updateStatus(
                failedMessageId,
                new FailedMessageStatus(RESEND, Instant.now(clock).plus(duration))
        );
    }
}
