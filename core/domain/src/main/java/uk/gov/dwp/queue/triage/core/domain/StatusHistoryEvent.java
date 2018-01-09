package uk.gov.dwp.queue.triage.core.domain;

import java.time.Instant;

public class StatusHistoryEvent {

    private final Status status;
    private final Instant updatedDateTime;

    public static StatusHistoryEvent statusHistoryEvent(Status status) {
        return new StatusHistoryEvent(status, Instant.now());
    }

    public StatusHistoryEvent(Status status, Instant updatedDateTime) {
        this.status = status;
        this.updatedDateTime = updatedDateTime;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getEffectiveDateTime() {
        return updatedDateTime;
    }

    public enum Status {
        FAILED,
        CLASSIFIED,
        RESEND,
        SENT,
        DELETED,
    }
}
