package uk.gov.dwp.queue.triage.core.resource.update;

import uk.gov.dwp.queue.triage.core.client.update.FailedMessageUpdateRequest;
import uk.gov.dwp.queue.triage.core.client.update.UpdateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

public class UpdateFailedMessageResource implements UpdateFailedMessageClient {

    private final FailedMessageService failedMessageService;

    public UpdateFailedMessageResource(FailedMessageService failedMessageService) {
        this.failedMessageService = failedMessageService;
    }

    @Override
    public void update(FailedMessageId failedMessageId, FailedMessageUpdateRequest failedMessageUpdateRequest) {
        failedMessageService.update(failedMessageId, failedMessageUpdateRequest.getUpdateRequests());
    }


}
