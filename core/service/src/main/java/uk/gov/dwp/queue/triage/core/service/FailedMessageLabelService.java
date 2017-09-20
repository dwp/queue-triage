package uk.gov.dwp.queue.triage.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

public class FailedMessageLabelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageLabelService.class);

    private final FailedMessageDao failedMessageDao;

    public FailedMessageLabelService(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    public void addLabel(FailedMessageId failedMessageId, String label) {
        failedMessageDao.addLabel(failedMessageId, label);
        LOGGER.debug("Added label '{}' to FailedMessage: {}", label, failedMessageId);
    }

    public void removeLabel(FailedMessageId failedMessageId, String label) {
        failedMessageDao.removeLabel(failedMessageId, label);
        LOGGER.debug("Removed label '{}' from FailedMessage: {}", label, failedMessageId);
    }
}
