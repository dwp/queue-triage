package uk.gov.dwp.queue.triage.core.resend;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.time.Instant;
import java.util.function.Predicate;

public class HistoricStatusPredicate implements Predicate<FailedMessage> {
    @Override
    public boolean test(FailedMessage failedMessage) {
        return failedMessage.getFailedMessageStatus().getEffectiveDateTime().isBefore(Instant.now());
    }
}
