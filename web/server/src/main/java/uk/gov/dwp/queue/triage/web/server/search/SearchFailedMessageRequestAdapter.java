package uk.gov.dwp.queue.triage.web.server.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;

public class SearchFailedMessageRequestAdapter {

    public SearchFailedMessageRequest adapt(SearchW2UIRequest request) {
        return newSearchFailedMessageRequest().build();
    }
}
