package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

public interface UpdateRequestAdapter<T extends UpdateRequest> {

    void adapt(T updateRequest, FailedMessageBuilder failedMessageBuilder);
}
