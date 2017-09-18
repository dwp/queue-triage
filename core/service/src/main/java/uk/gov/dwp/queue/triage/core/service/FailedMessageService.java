package uk.gov.dwp.queue.triage.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.failedMessageStatus;

public class FailedMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageService.class);

    private final FailedMessageDao failedMessageDao;

    public FailedMessageService(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    public void create(FailedMessage failedMessage) {
        failedMessageDao.insert(failedMessage);
    }

    public void updateStatus(FailedMessageId failedMessageId, Status status) {
        updateStatus(failedMessageId, failedMessageStatus(status));
    }

    public void updateStatus(FailedMessageId failedMessageId, FailedMessageStatus failedMessageStatus) {
        LOGGER.debug("Message {} updated to {} with effectiveDateTime {}",
                failedMessageId,
                failedMessageStatus.getStatus(),
                failedMessageStatus.getEffectiveDateTime()
        );
        failedMessageDao.updateStatus(failedMessageId, failedMessageStatus);
    }

    public void delete(FailedMessageId failedMessageId) {
        LOGGER.debug("Message {} deleted", failedMessageId);
        failedMessageDao.updateStatus(failedMessageId, failedMessageStatus(DELETED));
    }
}
