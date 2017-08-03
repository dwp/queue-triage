package uk.gov.dwp.queue.triage.core.resource.resend;

import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.RESEND;

public class ResendFailedMessageResource implements ResendFailedMessageClient {

    private final FailedMessageService failedMessageService;

    public ResendFailedMessageResource(FailedMessageService failedMessageService) {
        this.failedMessageService = failedMessageService;
    }

    @Override
    public void resendFailedMessage(FailedMessageId failedMessageId) {
        failedMessageService.updateStatus(failedMessageId, RESEND);
    }
}
