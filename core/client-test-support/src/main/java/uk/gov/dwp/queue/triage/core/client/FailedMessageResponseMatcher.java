package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

public class FailedMessageResponseMatcher extends TypeSafeMatcher<FailedMessageResponse> {

    private Matcher<FailedMessageId> failedMessageIdMatcher = new IsAnything<>();
    private Matcher<String> contentMatcher = new IsAnything<>();
    private Matcher<String> brokerMatcher = new IsAnything<>();
    private Matcher<Optional<String>> destinationMatcher = new IsAnything<>();
    private Matcher<ZonedDateTime> sentAtMatcher = new IsAnything<>();
    private Matcher<ZonedDateTime> failedAtMatcher = new IsAnything<>();
    private Matcher<Map<? extends String, ? extends Object>> propertiesMatcher = new IsAnything<>();

    private FailedMessageResponseMatcher() { }

    public static FailedMessageResponseMatcher aFailedMessage() {
        return new FailedMessageResponseMatcher();
    }

    public FailedMessageResponseMatcher withFailedMessageId(Matcher<FailedMessageId> failedMessageIdMatcher) {
        this.failedMessageIdMatcher = failedMessageIdMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withContent(Matcher<String> contentMatcher) {
        this.contentMatcher = contentMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withBroker(Matcher<String> brokerNameMatcher) {
        this.brokerMatcher = brokerNameMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withDestination(Matcher<Optional<String>> destinationNameMatcher) {
        this.destinationMatcher = destinationNameMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withSentAt(ZonedDateTime sentAt) {
        this.sentAtMatcher = equalTo(sentAt);
        return this;
    }

    public FailedMessageResponseMatcher withSentAt(Matcher<ZonedDateTime> sentAtMatcher) {
        this.sentAtMatcher = sentAtMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withFailedAt(ZonedDateTime failedAt) {
        this.failedAtMatcher = equalTo(failedAt);
        return this;
    }

    public FailedMessageResponseMatcher withFailedAt(Matcher<ZonedDateTime> failedAtMatcher) {
        this.failedAtMatcher = failedAtMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withProperties(Matcher<Map<? extends String, ? extends Object>> propertiesMatcher) {
        this.propertiesMatcher = propertiesMatcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(FailedMessageResponse item) {
        return failedMessageIdMatcher.matches(item.getFailedMessageId())
                && contentMatcher.matches(item.getContent())
                && brokerMatcher.matches(item.getBroker())
                && destinationMatcher.matches(item.getDestination())
                && sentAtMatcher.matches(item.getSentAt())
                && failedAtMatcher.matches(item.getFailedAt())
                && propertiesMatcher.matches(item.getProperties());
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("failedMessageId is ").appendDescriptionOf(failedMessageIdMatcher)
                .appendText(" content is ").appendDescriptionOf(contentMatcher)
                .appendText(" broker is ").appendDescriptionOf(brokerMatcher)
                .appendText(" destination is ").appendDescriptionOf(destinationMatcher)
                .appendText(" sentAt is ").appendDescriptionOf(sentAtMatcher)
                .appendText(" failedAt is ").appendDescriptionOf(failedAtMatcher)
                .appendText(" properties are ").appendDescriptionOf(propertiesMatcher)
        ;
    }
}