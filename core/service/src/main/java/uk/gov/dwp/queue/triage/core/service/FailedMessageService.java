package uk.gov.dwp.queue.triage.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Collections;
import java.util.List;

import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest.statusUpdateRequest;

public class FailedMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageService.class);

    private final FailedMessageDao failedMessageDao;
    private final FailedMessageBuilderFactory failedMessageBuilderFactory;

    public FailedMessageService(FailedMessageDao failedMessageDao,
                                FailedMessageBuilderFactory failedMessageBuilderFactory) {
        this.failedMessageDao = failedMessageDao;
        this.failedMessageBuilderFactory = failedMessageBuilderFactory;
    }

    public void create(FailedMessage failedMessage) {
        failedMessageDao.insert(failedMessage);
    }

    public void delete(FailedMessageId failedMessageId) {
        LOGGER.debug("Message {} deleted", failedMessageId);
        update(failedMessageId, statusUpdateRequest(DELETED));
    }

    public <T extends UpdateRequest> void update(FailedMessageId failedMessageId, T updateRequest) {
        update(failedMessageId, Collections.singletonList(updateRequest));
    }

    public void update(FailedMessageId failedMessageId, List<? extends UpdateRequest> updateRequests) {
        if (!updateRequests.isEmpty()) {
            failedMessageDao.update(failedMessageBuilderFactory
                    .create(failedMessageId)
                    .apply(updateRequests)
                    .build());
        }
    }

}
