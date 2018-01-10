package uk.gov.dwp.queue.triage.core.domain;

import java.time.Instant;

public class StatusHistoryEvent {

    private final Status status;
    private final Instant effectiveDateTimme;

    public static StatusHistoryEvent statusHistoryEvent(Status status) {
        return new StatusHistoryEvent(status, Instant.now());
    }

    public StatusHistoryEvent(Status status, Instant effectiveDateTimme) {
        this.status = status;
        this.effectiveDateTimme = effectiveDateTimme;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getEffectiveDateTime() {
        return effectiveDateTimme;
    }

    public enum Status {
        FAILED,
        CLASSIFIED,
        RESEND,
        SENT,
        DELETED,
    }
}
