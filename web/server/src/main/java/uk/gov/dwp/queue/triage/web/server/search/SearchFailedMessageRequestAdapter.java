package uk.gov.dwp.queue.triage.web.server.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.SearchFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAnyCriteria;

public class SearchFailedMessageRequestAdapter {

    public SearchFailedMessageRequest adapt(SearchW2UIRequest request) {
        SearchFailedMessageRequestBuilder searchFailedMessageRequestBuilder = searchMatchingAnyCriteria();
        for (Criteria criteria : request.getSearchCriteria()) {
            criteria.addToSearchRequest(searchFailedMessageRequestBuilder);
        }
        return searchFailedMessageRequestBuilder.build();
    }
}
