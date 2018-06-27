package uk.gov.dwp.queue.triage.core.service;

import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.UpdateRequestAdapterRegistry;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.function.Function;
import java.util.function.Predicate;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageNotFoundException.failedMessageNotFound;

public class FailedMessageBuilderFactory {

    private static final Predicate<FailedMessage> IS_DELETED = x -> x.getStatus().equals(StatusHistoryEvent.Status.DELETED);
    private final UpdateRequestAdapterRegistry updateRequestAdapterRegistry;
    private final FailedMessageDao failedMessageDao;

    public FailedMessageBuilderFactory(UpdateRequestAdapterRegistry updateRequestAdapterRegistry,
                                       FailedMessageDao failedMessageDao) {
        this.updateRequestAdapterRegistry = updateRequestAdapterRegistry;
        this.failedMessageDao = failedMessageDao;
    }

    public FailedMessageBuilder create(FailedMessageId failedMessageId) {
        return failedMessageDao.findById(failedMessageId)
                .filter(IS_DELETED.negate())
                .map(createFailedMessageBuilder())
                .orElseThrow(failedMessageNotFound(failedMessageId))
                .withUpdateRequestAdapterRegistry(updateRequestAdapterRegistry);
    }

    protected Function<FailedMessage, FailedMessageBuilder> createFailedMessageBuilder() {
        return FailedMessageBuilder::clone;
    }

}
