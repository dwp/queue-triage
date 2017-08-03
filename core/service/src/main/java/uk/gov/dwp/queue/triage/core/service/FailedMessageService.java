package uk.gov.dwp.queue.triage.core.service;

import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.failedMessageStatus;

public class FailedMessageService {

    private final FailedMessageDao failedMessageDao;

    public FailedMessageService(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    public void create(FailedMessage failedMessage) {
        failedMessageDao.insert(failedMessage);
    }

    public void updateStatus(FailedMessageId failedMessageId, FailedMessageStatus.Status status) {
        failedMessageDao.updateStatus(failedMessageId, failedMessageStatus(status));
    }
}
