package uk.gov.dwp.queue.triage.core.service.processor;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

public class NewFailedMessageProcessor implements FailedMessageProcessor {

    private final FailedMessageService failedMessageService;

    public NewFailedMessageProcessor(FailedMessageService failedMessageService) {
        this.failedMessageService = failedMessageService;
    }

    @Override
    public void process(FailedMessage failedMessage) {
        failedMessageService.create(failedMessage);
    }
}
