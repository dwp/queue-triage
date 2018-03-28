package uk.gov.dwp.queue.triage.core.client.search;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.Operator;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

public class SearchFailedMessageRequestMatcher extends TypeSafeMatcher<SearchFailedMessageRequest> {

    private final Matcher<Operator> operatorMatcher;
    private Matcher<Optional<String>> brokerMatcher = new IsAnything<>();
    private Matcher<Optional<String>> contentMatcher = new IsAnything<>();
    private Matcher<Optional<String>> destinationMatcher = new IsAnything<>();
    private Matcher<Optional<String>> jmsMessageIdMatcher = new IsAnything<>();
    private Matcher<Iterable<? extends FailedMessageStatus>> statusMatcher = new IsAnything<>();

    private SearchFailedMessageRequestMatcher(Matcher<Operator> operatorMatcher) {
        this.operatorMatcher = operatorMatcher;
    }

    @Override
    protected boolean matchesSafely(SearchFailedMessageRequest searchFailedMessageRequest) {
        return operatorMatcher.matches(searchFailedMessageRequest.getOperator())
                && brokerMatcher.matches(searchFailedMessageRequest.getBroker())
                && contentMatcher.matches(searchFailedMessageRequest.getContent())
                && destinationMatcher.matches(searchFailedMessageRequest.getDestination())
                && statusMatcher.matches(searchFailedMessageRequest.getStatuses())
                && jmsMessageIdMatcher.matches(searchFailedMessageRequest.getJmsMessageId())
                ;
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("operator ").appendDescriptionOf(operatorMatcher)
                .appendText(" broker ").appendDescriptionOf(brokerMatcher)
                .appendText(" content ").appendDescriptionOf(contentMatcher)
                .appendText(" destination ").appendDescriptionOf(destinationMatcher)
                .appendText(" status ").appendDescriptionOf(statusMatcher)
                .appendText(" jmsMessageId ").appendDescriptionOf(jmsMessageIdMatcher)
        ;
    }

    public static SearchFailedMessageRequestMatcher aSearchRequestMatchingAllCriteria() {
        return new SearchFailedMessageRequestMatcher(equalTo(Operator.AND));
    }

    public static SearchFailedMessageRequestMatcher aSearchRequestMatchingAnyCriteria() {
        return new SearchFailedMessageRequestMatcher(equalTo(Operator.OR));
    }

    public SearchFailedMessageRequestMatcher withBroker(Matcher<Optional<String>> brokerMatcher) {
        this.brokerMatcher = brokerMatcher;
        return this;
    }

    public SearchFailedMessageRequestMatcher withBroker(String broker) {
        this.brokerMatcher = equalTo(Optional.of(broker));
        return this;
    }

    public SearchFailedMessageRequestMatcher withContent(Matcher<Optional<String>> contentMatcher) {
        this.contentMatcher = contentMatcher;
        return this;
    }

    public SearchFailedMessageRequestMatcher withContent(String content) {
        this.contentMatcher = equalTo(Optional.of(content));
        return this;
    }

    public SearchFailedMessageRequestMatcher withDestination(Matcher<Optional<String>> destinationMatcher) {
        this.destinationMatcher = destinationMatcher;
        return this;
    }

    public SearchFailedMessageRequestMatcher withDestination(String destination) {
        this.destinationMatcher = equalTo(Optional.of(destination));
        return this;
    }

    public SearchFailedMessageRequestMatcher withStatusMatcher(Matcher<Iterable<? extends FailedMessageStatus>> statusMatcher) {
        this.statusMatcher = statusMatcher;
        return this;
    }

    public  SearchFailedMessageRequestMatcher withNoJmsMessageId() {
        this.jmsMessageIdMatcher = equalTo(Optional.empty());
        return this;
    }

    public  SearchFailedMessageRequestMatcher withJmsMessageId(String jmsMessageId) {
        this.jmsMessageIdMatcher = equalTo(Optional.of(jmsMessageId));
        return this;
    }
}
