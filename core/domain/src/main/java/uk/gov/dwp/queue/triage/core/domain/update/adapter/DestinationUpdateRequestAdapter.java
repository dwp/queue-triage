package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import uk.gov.dwp.queue.triage.core.client.update.DestinationUpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

import java.util.Optional;

public class DestinationUpdateRequestAdapter implements UpdateRequestAdapter<DestinationUpdateRequest> {

    @Override
    public void adapt(DestinationUpdateRequest updateRequest, FailedMessageBuilder failedMessageBuilder) {
        failedMessageBuilder.withDestination(new Destination(updateRequest.getBroker(), Optional.ofNullable(updateRequest.getDestination())));
    }
}
