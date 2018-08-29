package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JacksonInject;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

public class DeleteMessageAction implements FailedMessageAction {

    private final FailedMessageService failedMessageService;

    public DeleteMessageAction(@JacksonInject FailedMessageService failedMessageService) {
        this.failedMessageService = failedMessageService;
    }

    @Override
    public void accept(FailedMessage failedMessage) {
        failedMessageService.delete(failedMessage.getFailedMessageId());
    }

    @Override
    public String toString() {
        return "delete failedMessage";
    }
}
