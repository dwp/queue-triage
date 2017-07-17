package uk.gov.dwp.queue.triage.core.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;

import java.util.Collection;

public interface FailedMessageSearchService {

    Collection<SearchFailedMessageResponse> search(SearchFailedMessageRequest request);
}
