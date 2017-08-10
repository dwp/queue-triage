package uk.gov.dwp.queue.triage.core.resend;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.SENT;

public class FailedMessageResender {

    private final MessageSender messageSender;
    private final FailedMessageService failedMessageService;

    public FailedMessageResender(MessageSender messageSender,
                                 FailedMessageService failedMessageService) {
        this.messageSender = messageSender;
        this.failedMessageService = failedMessageService;
    }

    public void send(FailedMessage failedMessage) {
        messageSender.send(failedMessage);
        failedMessageService.updateStatus(failedMessage.getFailedMessageId(), SENT);
    }
}
