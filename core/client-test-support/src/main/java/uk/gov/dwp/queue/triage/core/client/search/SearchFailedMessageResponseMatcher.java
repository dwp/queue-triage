package uk.gov.dwp.queue.triage.core.client.search;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
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
    private Matcher<FailedMessageStatus> status = new IsAnything<>();
    private Matcher<Instant> statusDateTime = new IsAnything<>();
    private Matcher<Iterable<? extends String>> labels = new IsAnything<>();

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

    public SearchFailedMessageResponseMatcher withStatus(FailedMessageStatus status) {
        this.status = equalTo(status);
        return this;
    }

    public SearchFailedMessageResponseMatcher withStatusDateTime(Instant statusDateTime) {
        this.statusDateTime = equalTo(statusDateTime);
        return this;
    }

    public SearchFailedMessageResponseMatcher withStatusDateTime(Matcher<Instant> statusDateTimeMatcher) {
        this.statusDateTime = statusDateTimeMatcher;
        return this;
    }

    public SearchFailedMessageResponseMatcher withLabels(Matcher<Iterable<? extends String>> labelsMatcher) {
        this.labels = labelsMatcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(SearchFailedMessageResponse item) {
        return failedMessageId.matches(item.getFailedMessageId())
                && jmsMessageId.matches(item.getJmsMessageId())
                && content.matches(item.getContent())
                && broker.matches(item.getBroker())
                && destination.matches(item.getDestination())
                && status.matches(item.getStatus())
                && statusDateTime.matches(item.getStatusDateTime())
                && labels.matches(item.getLabels())
                ;
    }

    @Override
    public void describeTo(Description description) {
        appendIfMatcherIsNotIsAnything("failedMessageId is ", failedMessageId, description);
        appendIfMatcherIsNotIsAnything(" jmsMessageId is ", jmsMessageId, description);
        appendIfMatcherIsNotIsAnything(" content is ", content, description);
        appendIfMatcherIsNotIsAnything(" broker is ", broker, description);
        appendIfMatcherIsNotIsAnything(" destination is ", destination, description);
        appendIfMatcherIsNotIsAnything(" status is ", status, description);
        appendIfMatcherIsNotIsAnything(" statusDateTime is ", statusDateTime, description);
        appendIfMatcherIsNotIsAnything(" labels is ", labels, description);
    }

    private void appendIfMatcherIsNotIsAnything(String text, Matcher<?> fieldMatcher, Description description) {
        if (!IsAnything.class.equals(fieldMatcher.getClass())) {
            description.appendText(text).appendDescriptionOf(fieldMatcher);
        }
    }
}