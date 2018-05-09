package uk.gov.dwp.queue.triage.core.domain.update;

import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status;

import java.time.Instant;

public class StatusUpdateRequest implements UpdateRequest {

    private final Status status;
    private final Instant effectiveDateTime;

    public StatusUpdateRequest(Status status, Instant effectiveDateTime) {
        this.status = status;
        this.effectiveDateTime = effectiveDateTime;
    }

    public static StatusUpdateRequest statusUpdateRequest(Status status) {
        return new StatusUpdateRequest(status, Instant.now());
    }

    public Status getStatus() {
        return status;
    }

    public Instant getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public StatusHistoryEvent getStatusHistoryEvent() {
        return new StatusHistoryEvent(status, effectiveDateTime);
    }

    @Override
    public String toString() {
        return "StatusUpdateRequest{" +
                "status=" + status +
                ", effectiveDateTime=" + effectiveDateTime +
                '}';
    }
}
