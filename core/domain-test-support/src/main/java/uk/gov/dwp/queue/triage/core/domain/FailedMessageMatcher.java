package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class FailedMessageMatcher extends TypeSafeMatcher<FailedMessage> {

    private Matcher<FailedMessageId> failedMessageIdMatcher = new IsAnything<>();
    private Matcher<String> contentMatcher = new IsAnything<>();
    private Matcher<Destination> destinationMatcher = new IsAnything<>();
    private Matcher<ZonedDateTime> sentAtMatcher = new IsAnything<>();
    private Matcher<ZonedDateTime> failedAtMatcher = new IsAnything<>();
    private Matcher<Map<? extends String, ? extends Object>> propertiesMatcher = new IsAnything<>();

    private FailedMessageMatcher() { }

    public static FailedMessageMatcher aFailedMessage() {
        return new FailedMessageMatcher();
    }

    public FailedMessageMatcher withFailedMessageId(Matcher<FailedMessageId> failedMessageIdMatcher) {
        this.failedMessageIdMatcher = failedMessageIdMatcher;
        return this;
    }

    public FailedMessageMatcher withContent(Matcher<String> contentMatcher) {
        this.contentMatcher = contentMatcher;
        return this;
    }

    public FailedMessageMatcher withDestination(Matcher<Destination> destinationMatcher) {
        this.destinationMatcher = destinationMatcher;
        return this;
    }

    public FailedMessageMatcher withSentAt(ZonedDateTime sentAt) {
        this.sentAtMatcher = equalTo(sentAt);
        return this;
    }

    public FailedMessageMatcher withSentAt(Matcher<ZonedDateTime> sentAtMatcher) {
        this.sentAtMatcher = sentAtMatcher;
        return this;
    }

    public FailedMessageMatcher withFailedAt(ZonedDateTime failedAt) {
        this.failedAtMatcher = equalTo(failedAt);
        return this;
    }

    public FailedMessageMatcher withFailedAt(Matcher<ZonedDateTime> failedAtMatcher) {
        this.failedAtMatcher = failedAtMatcher;
        return this;
    }

    public FailedMessageMatcher withProperties(Matcher<Map<? extends String, ? extends Object>> propertiesMatcher) {
        this.propertiesMatcher = propertiesMatcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(FailedMessage item) {
        return failedMessageIdMatcher.matches(item.getFailedMessageId())
                && contentMatcher.matches(item.getContent())
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
                .appendText(" destination is ").appendDescriptionOf(destinationMatcher)
                .appendText(" sentAt is ").appendDescriptionOf(sentAtMatcher)
                .appendText(" failedAt is ").appendDescriptionOf(failedAtMatcher)
                .appendText(" properties are ").appendDescriptionOf(propertiesMatcher)
        ;
    }
}