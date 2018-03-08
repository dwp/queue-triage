package uk.gov.dwp.queue.triage.core.service.processor;

import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.function.Predicate;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;

public class UniqueFailedMessageIdPredicate implements Predicate<FailedMessage> {

    private final FailedMessageDao failedMessageDao;

    public UniqueFailedMessageIdPredicate(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return failedMessageDao.findById(failedMessage.getFailedMessageId()) == null;
    }
}
