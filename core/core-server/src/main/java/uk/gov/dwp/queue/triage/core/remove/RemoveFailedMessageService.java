package uk.gov.dwp.queue.triage.core.remove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;

public class RemoveFailedMessageService {

    private final FailedMessageDao failedMessageDao;
    private final Logger logger;

    public RemoveFailedMessageService(FailedMessageDao failedMessageDao) {
        this(failedMessageDao, LoggerFactory.getLogger(RemoveFailedMessageService.class));
    }

    RemoveFailedMessageService(FailedMessageDao failedMessageDao, Logger logger) {
        this.failedMessageDao = failedMessageDao;
        this.logger = logger;
    }

    public void removeFailedMessages() {
        try {
            logger.info("Attempting to remove FailedMessages");
            final long count = failedMessageDao.removeFailedMessages();
            logger.info("Successfully removed {} messages", count);
        } catch (Exception e) {
            logger.error("Could not remove messages", e);
        }
    }
}
