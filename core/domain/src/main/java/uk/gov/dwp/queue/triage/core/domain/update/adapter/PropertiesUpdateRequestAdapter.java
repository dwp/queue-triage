package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import uk.gov.dwp.queue.triage.core.client.update.PropertiesUpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

public class PropertiesUpdateRequestAdapter implements UpdateRequestAdapter<PropertiesUpdateRequest> {

    @Override
    public void adapt(PropertiesUpdateRequest updateRequest, FailedMessageBuilder failedMessageBuilder) {
        updateRequest.getDeletedProperties().forEach(failedMessageBuilder::removeProperty);
        updateRequest.getUpdatedProperties().forEach(failedMessageBuilder::withProperty);
    }
}
