package uk.gov.dwp.queue.triage.core.resource.search;

import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Collection;

import static java.lang.String.format;

public class FailedMessageSearchResource implements SearchFailedMessageClient {

    private final FailedMessageDao failedMessageDao;
    private final FailedMessageResponseFactory failedMessageResponseFactory;
    private final FailedMessageSearchService failedMessageSearchService;

    public FailedMessageSearchResource(FailedMessageDao failedMessageDao,
                                       FailedMessageResponseFactory failedMessageResponseFactory,
                                       FailedMessageSearchService failedMessageSearchService) {
        this.failedMessageDao = failedMessageDao;
        this.failedMessageResponseFactory = failedMessageResponseFactory;
        this.failedMessageSearchService = failedMessageSearchService;
    }

    @Override
    public FailedMessageResponse getFailedMessage(FailedMessageId failedMessageId) {
        FailedMessage failedMessage = failedMessageDao.findById(failedMessageId);
        if (failedMessage == null) {
            throw new NotFoundException(format("Failed Message: %s not found", failedMessageId));
        }
        return failedMessageResponseFactory.create(failedMessage);
    }

    @Override
    public Collection<SearchFailedMessageResponse> search(SearchFailedMessageRequest request) {
        // TODO: Introduce javax.validation
        if (request.getBroker() == null) {
            throw new BadRequestException("broker cannot be null");
        }
        return failedMessageSearchService.search(request);
    }
}
