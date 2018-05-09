package uk.gov.dwp.queue.triage.core.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Collection;
import java.util.Optional;

public interface FailedMessageSearchService {

    Collection<FailedMessage> search(SearchFailedMessageRequest request);

    Collection<FailedMessage> findByStatus(Status status);

    Optional<FailedMessage> findById(FailedMessageId failedMessageId);
}
