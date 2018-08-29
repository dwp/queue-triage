package uk.gov.dwp.queue.triage.core.domain;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.function.Supplier;

public class FailedMessageNotFoundException extends RuntimeException {
    public FailedMessageNotFoundException(FailedMessageId failedMessageId) {
        super(String.format("Failed Message: %s not found", failedMessageId));
    }

    public static Supplier<FailedMessageNotFoundException> failedMessageNotFound(FailedMessageId failedMessageId) {
        return () -> new FailedMessageNotFoundException(failedMessageId);
    }
}
