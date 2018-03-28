package uk.gov.dwp.queue.triage.core.service.processor;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.function.Predicate;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;

public class UniqueJmsMessageIdPredicate implements Predicate<FailedMessage> {

    private final FailedMessageSearchService failedMessageSearchService;

    public UniqueJmsMessageIdPredicate(FailedMessageSearchService failedMessageSearchService) {
        this.failedMessageSearchService = failedMessageSearchService;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return failedMessageSearchService.search(searchMatchingAllCriteria()
                .withBroker(failedMessage.getDestination().getBrokerName())
                .withJmsMessageId(failedMessage.getJmsMessageId())
                .build()
        ).isEmpty();
    }
}
