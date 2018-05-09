package uk.gov.dwp.queue.triage.core.domain;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

public class FailedMessageNotFoundException extends RuntimeException {
    public FailedMessageNotFoundException(FailedMessageId failedMessageId) {
        super(String.format("Failed Message: %s not found", failedMessageId));
    }
}
