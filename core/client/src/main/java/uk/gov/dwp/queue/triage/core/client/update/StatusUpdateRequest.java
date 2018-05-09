package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;

/**
 * NOTE: This class does not allow clients to set the effective date
 */
public class StatusUpdateRequest implements UpdateRequest {

    @JsonProperty
    private final FailedMessageStatus status;

    public StatusUpdateRequest(@JsonProperty("status") FailedMessageStatus status) {
        this.status = status;
    }

    public FailedMessageStatus getStatus() {
        return status;
    }
}
