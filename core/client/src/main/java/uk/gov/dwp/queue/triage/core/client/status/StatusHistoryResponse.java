package uk.gov.dwp.queue.triage.core.client.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;

import javax.validation.constraints.NotNull;
import java.time.Instant;

public class StatusHistoryResponse {

    @NotNull
    @JsonProperty
    private final FailedMessageStatus status;
    @NotNull
    @JsonProperty
    private final Instant effectiveDateTime;

    public StatusHistoryResponse(@JsonProperty("status") FailedMessageStatus status,
                                 @JsonProperty("effectiveDateTime") Instant effectiveDateTime) {
        this.status = status;
        this.effectiveDateTime = effectiveDateTime;
    }

    public FailedMessageStatus getStatus() {
        return status;
    }

    public Instant getEffectiveDateTime() {
        return effectiveDateTime;
    }

    @Override
    public String toString() {
        return "StatusHistoryResponse{" +
                "status=" + status +
                ", effectiveDateTime=" + effectiveDateTime +
                '}';
    }
}
