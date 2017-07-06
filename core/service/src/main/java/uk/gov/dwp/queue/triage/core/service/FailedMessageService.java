package uk.gov.dwp.queue.triage.core.service;

import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public class FailedMessageService {

    private final FailedMessageDao failedMessageDao;

    public FailedMessageService(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    public void create(FailedMessage failedMessage) {
        failedMessageDao.insert(failedMessage);
    }
}
