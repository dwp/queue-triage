package uk.gov.dwp.queue.triage.core.service.processor;

import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Predicate;

public class UniqueFailedMessageIdPredicate implements Predicate<FailedMessage> {

    private final FailedMessageDao failedMessageDao;

    public UniqueFailedMessageIdPredicate(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return !failedMessageDao.findById(failedMessage.getFailedMessageId()).isPresent();
    }
}
