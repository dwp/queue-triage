package uk.gov.dwp.queue.triage.core.resource.create;

import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;

public class CreateFailedMessageResource implements CreateFailedMessageClient {

    private final FailedMessageFactory failedMessageFactory;
    private final FailedMessageDao failedMessageDao;

    public CreateFailedMessageResource(FailedMessageFactory failedMessageFactory, FailedMessageDao failedMessageDao) {
        this.failedMessageFactory = failedMessageFactory;
        this.failedMessageDao = failedMessageDao;
    }

    public void create(CreateFailedMessageRequest failedMessageRequest) {
        failedMessageDao.insert(failedMessageFactory.create(failedMessageRequest));
    }
}
