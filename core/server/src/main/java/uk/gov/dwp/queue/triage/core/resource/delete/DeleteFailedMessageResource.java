package uk.gov.dwp.queue.triage.core.resource.delete;

import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

public class DeleteFailedMessageResource implements DeleteFailedMessageClient {

    private final FailedMessageService failedMessageService;

    public DeleteFailedMessageResource(FailedMessageService failedMessageService) {
        this.failedMessageService = failedMessageService;
    }

    @Override
    public void deleteFailedMessage(FailedMessageId failedMessageId) {
        failedMessageService.delete(failedMessageId);
    }
}
