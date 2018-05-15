package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import uk.gov.dwp.queue.triage.core.client.update.ContentUpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

public class ContentUpdateRequestAdapter implements UpdateRequestAdapter<ContentUpdateRequest> {

    @Override
    public void adapt(ContentUpdateRequest updateRequest, FailedMessageBuilder failedMessageBuilder) {
        failedMessageBuilder.withContent(updateRequest.getContent());
    }
}