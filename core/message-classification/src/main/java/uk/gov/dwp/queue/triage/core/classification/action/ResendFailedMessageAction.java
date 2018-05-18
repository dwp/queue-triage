package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import java.time.Clock;
import java.time.Duration;
import java.util.Optional;

import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.RESEND;

public class ResendFailedMessageAction implements FailedMessageAction {

    private final Duration resendDelay;
    private final FailedMessageService failedMessageService;
    private final Clock clock;

    public ResendFailedMessageAction(@JsonProperty("resendDelay") Duration resendDelay,
                                     @JacksonInject FailedMessageService failedMessageService) {
        this(resendDelay, failedMessageService, Clock.systemUTC());
    }

    ResendFailedMessageAction(Duration resendDelay,
                              FailedMessageService failedMessageService,
                              Clock clock) {
        this.resendDelay = resendDelay;
        this.failedMessageService = failedMessageService;
        this.clock = clock;
    }

    @Override
    public void accept(FailedMessage failedMessage) {
        failedMessageService.update(
                failedMessage.getFailedMessageId(),
                new StatusUpdateRequest(RESEND, clock.instant().plus(getResendDelay())));
    }

    private Duration getResendDelay() {
        return Optional.ofNullable(resendDelay).orElse(Duration.ofSeconds(0));
    }
}
