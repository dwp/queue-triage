package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;

public class StatusUpdateRequestAdapter implements UpdateRequestAdapter<StatusUpdateRequest> {

    @Override
    public void adapt(StatusUpdateRequest updateRequest, FailedMessageBuilder failedMessageBuilder) {
        failedMessageBuilder.withStatusHistoryEvent(new StatusHistoryEvent(updateRequest.getStatus(), updateRequest.getEffectiveDateTime()));
    }
}
