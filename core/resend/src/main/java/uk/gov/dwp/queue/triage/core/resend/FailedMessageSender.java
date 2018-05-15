package uk.gov.dwp.queue.triage.core.resend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.SENT;
import static uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest.statusUpdateRequest;

public class FailedMessageSender implements MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageSender.class);

    private final MessageSender messageSender;
    private final FailedMessageService failedMessageService;

    public FailedMessageSender(MessageSender messageSender,
                               FailedMessageService failedMessageService) {
        this.messageSender = messageSender;
        this.failedMessageService = failedMessageService;
    }

    public void send(FailedMessage failedMessage) {
        try {
            messageSender.send(failedMessage);
            failedMessageService.update(failedMessage.getFailedMessageId(), statusUpdateRequest(SENT));
        } catch (Exception e) {
            LOGGER.error("Could not send FailedMessage: " + failedMessage.getFailedMessageId(), e);
        }
    }
}
