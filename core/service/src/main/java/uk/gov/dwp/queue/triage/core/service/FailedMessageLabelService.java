package uk.gov.dwp.queue.triage.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Set;

public class FailedMessageLabelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageLabelService.class);

    private final FailedMessageDao failedMessageDao;

    public FailedMessageLabelService(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    public void addLabel(FailedMessageId failedMessageId, String label) {
        LOGGER.debug("Adding label '{}' to FailedMessage: {}", label, failedMessageId);
        failedMessageDao.addLabel(failedMessageId, label);
    }

    public void removeLabel(FailedMessageId failedMessageId, String label) {
        LOGGER.debug("Removing label '{}' from FailedMessage: {}", label, failedMessageId);
        failedMessageDao.removeLabel(failedMessageId, label);
    }

    public void setLabels(FailedMessageId failedMessageId, Set<String> labels) {
        LOGGER.debug("Replacing all labels on FailedMessage: {}", failedMessageId);
        failedMessageDao.setLabels(failedMessageId, labels);
    }
}
