package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

public class SearchFailedMessageResponseMatcher extends TypeSafeMatcher<SearchFailedMessageResponse> {

    private Matcher<FailedMessageId> failedMessageId = new IsAnything<>();
    private Matcher<String> jmsMessageId = new IsAnything<>();
    private Matcher<String> content = new IsAnything<>();
    private Matcher<String> broker = new IsAnything<>();
    private Matcher<Optional<String>> destination = new IsAnything<>();
    private Matcher<Instant> sentDateTime = new IsAnything<>();
    private Matcher<Instant> failedDateTime = new IsAnything<>();

    private SearchFailedMessageResponseMatcher() {
    }

    public static SearchFailedMessageResponseMatcher aFailedMessage() {
        return new SearchFailedMessageResponseMatcher();
    }

    public SearchFailedMessageResponseMatcher withFailedMessageId(Matcher<FailedMessageId> failedMessageIdMatcher) {
        this.failedMessageId = failedMessageIdMatcher;
        return this;
    }

    public SearchFailedMessageResponseMatcher withJmsMessageId(Matcher<String> jmsMessageId) {
        this.jmsMessageId = jmsMessageId;
        return this;
    }

    public SearchFailedMessageResponseMatcher withContent(Matcher<String> contentMatcher) {
        this.content = contentMatcher;
        return this;
    }

    public SearchFailedMessageResponseMatcher withBroker(Matcher<String> brokerNameMatcher) {
        this.broker = brokerNameMatcher;
        return this;
    }

    public SearchFailedMessageResponseMatcher withDestination(Matcher<Optional<String>> destinationNameMatcher) {
        this.destination = destinationNameMatcher;
        return this;
    }

    public SearchFailedMessageResponseMatcher withSentDateTime(Instant sentAt) {
        this.sentDateTime = equalTo(sentAt);
        return this;
    }

    public SearchFailedMessageResponseMatcher withSentDateTime(Matcher<Instant> sentDateTimeMatcher) {
        this.sentDateTime = sentDateTimeMatcher;
        return this;
    }

    public SearchFailedMessageResponseMatcher withFailedDateTime(Instant failedDateTime) {
        this.failedDateTime = equalTo(failedDateTime);
        return this;
    }

    public SearchFailedMessageResponseMatcher withFailedDateTime(Matcher<Instant> failedAtMatcher) {
        this.failedDateTime = failedAtMatcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(SearchFailedMessageResponse item) {
        return failedMessageId.matches(item.getFailedMessageId())
                && jmsMessageId.matches(item.getJmsMessageId())
                && content.matches(item.getContent())
                && broker.matches(item.getBroker())
                && destination.matches(item.getDestination())
                && sentDateTime.matches(item.getSentDateTime())
                && failedDateTime.matches(item.getLastFailedDateTime())
                ;
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("failedMessageId is ").appendDescriptionOf(failedMessageId)
                .appendText(" jmsMessageId is ").appendDescriptionOf(jmsMessageId)
                .appendText(" content is ").appendDescriptionOf(content)
                .appendText(" broker is ").appendDescriptionOf(broker)
                .appendText(" destination is ").appendDescriptionOf(destination)
                .appendText(" sentDateTime is ").appendDescriptionOf(sentDateTime)
                .appendText(" failedDateTime is ").appendDescriptionOf(failedDateTime)
        ;
    }
}