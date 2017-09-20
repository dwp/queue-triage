package uk.gov.dwp.queue.triage.core.resource.label;

import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;
import uk.gov.dwp.queue.triage.core.service.FailedMessageLabelService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

public class LabelFailedMessageResource implements LabelFailedMessageClient {

    private final FailedMessageLabelService failedMessageLabelService;

    public LabelFailedMessageResource(FailedMessageLabelService failedMessageLabelService) {
        this.failedMessageLabelService = failedMessageLabelService;
    }

    @Override
    public void addLabel(FailedMessageId failedMessageId, String label) {
        failedMessageLabelService.addLabel(failedMessageId, label);
    }

    @Override
    public void removeLabel(FailedMessageId failedMessageId, String label) {
        failedMessageLabelService.removeLabel(failedMessageId, label);
    }
}
