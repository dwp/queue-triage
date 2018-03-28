package uk.gov.dwp.queue.triage.core.domain;

import java.time.Instant;

public class StatusHistoryEvent {

    private final Status status;
    private final Instant effectiveDateTime;

    public static StatusHistoryEvent statusHistoryEvent(Status status) {
        return new StatusHistoryEvent(status, Instant.now());
    }

    public StatusHistoryEvent(Status status, Instant effectiveDateTime) {
        this.status = status;
        this.effectiveDateTime = effectiveDateTime;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getEffectiveDateTime() {
        return effectiveDateTime;
    }

    @Override
    public String toString() {
        return "StatusHistoryEvent{" +
                "status=" + status +
                ", effectiveDateTime=" + effectiveDateTime +
                '}';
    }

    public enum Status {
        FAILED,
        CLASSIFIED,
        RESEND,
        SENT,
        DELETED,
    }
}
