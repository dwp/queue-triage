package uk.gov.dwp.queue.triage.core.resource.search;

import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageResponseAdapter;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;

public class FailedMessageSearchResource implements SearchFailedMessageClient {

    private final FailedMessageDao failedMessageDao;
    private final FailedMessageResponseFactory failedMessageResponseFactory;
    private final FailedMessageSearchService failedMessageSearchService;
    private final SearchFailedMessageResponseAdapter searchFailedMessageResponseAdapter;

    public FailedMessageSearchResource(FailedMessageDao failedMessageDao,
                                       FailedMessageResponseFactory failedMessageResponseFactory,
                                       FailedMessageSearchService failedMessageSearchService,
                                       SearchFailedMessageResponseAdapter searchFailedMessageResponseAdapter) {
        this.failedMessageDao = failedMessageDao;
        this.failedMessageResponseFactory = failedMessageResponseFactory;
        this.failedMessageSearchService = failedMessageSearchService;
        this.searchFailedMessageResponseAdapter = searchFailedMessageResponseAdapter;
    }

    @Override
    public FailedMessageResponse getFailedMessage(FailedMessageId failedMessageId) {
        FailedMessage failedMessage = failedMessageDao.findById(failedMessageId);
        if ((failedMessage == null) || (DELETED.equals(failedMessage.getStatus()))) {
            throw new NotFoundException(format("Failed Message: %s not found", failedMessageId));
        }
        return failedMessageResponseFactory.create(failedMessage);
    }

    @Override
    public Collection<SearchFailedMessageResponse> search(SearchFailedMessageRequest request) {
        return failedMessageSearchService.search(request)
                .stream()
                .map(searchFailedMessageResponseAdapter::toResponse)
                .collect(Collectors.toList());
    }
}
