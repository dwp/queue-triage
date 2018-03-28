package uk.gov.dwp.queue.triage.core.service.processor;

import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public class ExistingFailedMessageProcessor implements FailedMessageProcessor {

    private final FailedMessageDao failedMessageDao;

    public ExistingFailedMessageProcessor(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    @Override
    public void process(FailedMessage failedMessage) {
        failedMessageDao.update(failedMessage);
    }
}
