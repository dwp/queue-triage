package uk.gov.dwp.queue.triage.web.server.api.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.web.server.api.Constants;

class StatusHistoryListItem {

    private final StatusHistoryResponse statusHistoryResponse;

    StatusHistoryListItem(StatusHistoryResponse statusHistoryResponse) {
        this.statusHistoryResponse = statusHistoryResponse;
    }

    @JsonProperty("recid")
    public String getRecId() {
        // Could this just be a UUID?
        return String.valueOf(statusHistoryResponse.getEffectiveDateTime().toEpochMilli()) + "-" + statusHistoryResponse.getStatus().toString().toLowerCase();
    }

    @JsonProperty
    public String getStatus() {
        return statusHistoryResponse.getStatus().getDescription();
    }

    @JsonProperty
    public String getEffectiveDateTime() {
        return Constants.toIsoDateTimeWithMs(statusHistoryResponse.getEffectiveDateTime()).orElse(null);
    }
}
