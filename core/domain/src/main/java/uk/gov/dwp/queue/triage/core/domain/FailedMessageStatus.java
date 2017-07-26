package uk.gov.dwp.queue.triage.core.domain;

import java.time.Instant;

public class FailedMessageStatus {

    private final Status status;
    private final Instant updatedDateTime;

    public static FailedMessageStatus failedMessageStatus(Status status) {
        return new FailedMessageStatus(status, Instant.now());
    }

    public FailedMessageStatus(Status status, Instant updatedDateTime) {
        this.status = status;
        this.updatedDateTime = updatedDateTime;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getUpdatedDateTime() {
        return updatedDateTime;
    }

    public enum Status {
        FAILED,
        RESEND,
        SENT,
        DELETED,
    }
}
