package uk.gov.dwp.queue.triage.core.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Collection;

public interface FailedMessageSearchService {

    Collection<FailedMessage> search(SearchFailedMessageRequest request);
}
