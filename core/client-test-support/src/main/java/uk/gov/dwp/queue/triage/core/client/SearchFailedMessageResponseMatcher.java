package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
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
        appendIfMatcherIsNotIsAnything("failedMessageId is ", failedMessageId, description);
        appendIfMatcherIsNotIsAnything(" jmsMessageId is ", jmsMessageId, description);
        appendIfMatcherIsNotIsAnything(" content is ", content, description);
        appendIfMatcherIsNotIsAnything(" broker is ", broker, description);
        appendIfMatcherIsNotIsAnything(" destination is ", destination, description);
        appendIfMatcherIsNotIsAnything(" sentDateTime is ", sentDateTime, description);
        appendIfMatcherIsNotIsAnything(" failedDateTime is ", failedDateTime, description);
    }

    private void appendIfMatcherIsNotIsAnything(String text, Matcher<?> fieldMatcher, Description description) {
        if (!IsAnything.class.equals(fieldMatcher.getClass())) {
            description.appendText(text).appendDescriptionOf(fieldMatcher);
        }
    }
}