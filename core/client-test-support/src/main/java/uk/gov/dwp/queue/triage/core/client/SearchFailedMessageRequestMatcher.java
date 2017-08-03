package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;

import java.util.Optional;

public class SearchFailedMessageRequestMatcher extends TypeSafeMatcher<SearchFailedMessageRequest> {

    private Matcher<String> brokerMatcher;
    private Matcher<Iterable<? extends FailedMessageStatus>> statusMatcher;
    private Matcher<Optional<String>> destinationMatcher;

    @Override
    protected boolean matchesSafely(SearchFailedMessageRequest searchFailedMessageRequest) {
        return brokerMatcher.matches(searchFailedMessageRequest.getBroker())
                && destinationMatcher.matches(searchFailedMessageRequest.getDestination())
                && statusMatcher.matches(searchFailedMessageRequest.getStatuses())
                ;
    }

    @Override
    public void describeTo(Description description) {

    }

    public static SearchFailedMessageRequestMatcher aSearchRequest() {
        return new SearchFailedMessageRequestMatcher();
    }

    public SearchFailedMessageRequestMatcher withBrokerMatcher(Matcher<String> brokerMatcher) {
        this.brokerMatcher = brokerMatcher;
        return this;
    }

    public SearchFailedMessageRequestMatcher withDestination(Matcher<Optional<String>> destinationMatcher) {
        this.destinationMatcher = destinationMatcher;
        return this;
    }

    public SearchFailedMessageRequestMatcher withStatusMatcher(Matcher<Iterable<? extends FailedMessageStatus>> statusMatcher) {
        this.statusMatcher = statusMatcher;
        return this;
    }
}
