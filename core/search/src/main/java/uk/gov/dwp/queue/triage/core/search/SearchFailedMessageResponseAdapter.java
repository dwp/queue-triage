package uk.gov.dwp.queue.triage.core.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;

public class SearchFailedMessageResponseAdapter {

    public SearchFailedMessageResponse toResponse(FailedMessage failedMessage) {
        return newSearchFailedMessageResponse()
                .withBroker(failedMessage.getDestination().getBrokerName())
                .withContent(failedMessage.getContent())
                .withDestination(failedMessage.getDestination().getName().orElse(null))
                .withStatus(FailedMessageStatusAdapter.toFailedMessageStatus(failedMessage.getStatusHistoryEvent().getStatus()))
                .withStatusDateTime(failedMessage.getStatusHistoryEvent().getEffectiveDateTime())
                .withFailedMessageId(failedMessage.getFailedMessageId())
                .withJmsMessageId(failedMessage.getJmsMessageId())
                .withLabels(failedMessage.getLabels())
                .build();
    }
}
