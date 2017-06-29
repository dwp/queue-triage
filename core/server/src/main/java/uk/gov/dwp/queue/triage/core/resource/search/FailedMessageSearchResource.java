package uk.gov.dwp.queue.triage.core.resource.search;

import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.NotFoundException;

import static java.lang.String.format;

public class FailedMessageSearchResource implements SearchFailedMessageClient {

    private final FailedMessageDao failedMessageDao;
    private final FailedMessageResponseFactory failedMessageResponseFactory;

    public FailedMessageSearchResource(FailedMessageDao failedMessageDao,
                                       FailedMessageResponseFactory failedMessageResponseFactory) {
        this.failedMessageDao = failedMessageDao;
        this.failedMessageResponseFactory = failedMessageResponseFactory;
    }

    @Override
    public FailedMessageResponse getFailedMessage(FailedMessageId failedMessageId) {
        FailedMessage failedMessage = failedMessageDao.findById(failedMessageId);
        if (failedMessage == null) {
            throw new NotFoundException(format("Failed Message: %s not found", failedMessageId));
        }
        return failedMessageResponseFactory.create(failedMessage);
    }
}
