package uk.gov.dwp.queue.triage.core.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;

public class SearchFailedMessageResponseAdapter {

    public SearchFailedMessageResponse toResponse(FailedMessage failedMessage) {
        return newSearchFailedMessageResponse()
                .withBroker(failedMessage.getDestination().getBrokerName())
                .withContent(failedMessage.getContent())
                .withDestination(failedMessage.getDestination().getName())
                .withFailedDateTime(failedMessage.getFailedAt())
                .withFailedMessageId(failedMessage.getFailedMessageId())
                .withSentDateTime(failedMessage.getSentAt())
                .withLabels(failedMessage.getLabels())
                .build();
    }
}
