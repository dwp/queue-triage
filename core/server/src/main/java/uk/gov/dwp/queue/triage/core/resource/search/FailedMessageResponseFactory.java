package uk.gov.dwp.queue.triage.core.resource.search;

import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter;

public class FailedMessageResponseFactory {

    private final FailedMessageStatusAdapter failedMessageStatusAdapter;

    public FailedMessageResponseFactory(FailedMessageStatusAdapter failedMessageStatusAdapter) {
        this.failedMessageStatusAdapter = failedMessageStatusAdapter;
    }

    public FailedMessageResponse create(FailedMessage failedMessage) {
        return new FailedMessageResponse(
                failedMessage.getFailedMessageId(),
                failedMessage.getDestination().getBrokerName(),
                failedMessage.getDestination().getName(),
                failedMessage.getSentAt(),
                failedMessage.getFailedAt(),
                failedMessage.getContent(),
                failedMessageStatusAdapter.toFailedMessageStatus(failedMessage.getStatusHistoryEvent().getStatus()),
                failedMessage.getProperties(),
                failedMessage.getLabels());
    }
}
