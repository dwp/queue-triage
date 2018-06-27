package uk.gov.dwp.queue.triage.core.domain;

import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;

public class FailedMessageResponseFactory {

    public FailedMessageResponse create(FailedMessage failedMessage) {
        return new FailedMessageResponse(
                failedMessage.getFailedMessageId(),
                failedMessage.getDestination().getBrokerName(),
                failedMessage.getDestination().getName(),
                failedMessage.getSentAt(),
                failedMessage.getFailedAt(),
                failedMessage.getContent(),
                FailedMessageStatusAdapter.toFailedMessageStatus(failedMessage.getStatusHistoryEvent().getStatus()),
                failedMessage.getProperties(),
                failedMessage.getLabels());
    }
}
