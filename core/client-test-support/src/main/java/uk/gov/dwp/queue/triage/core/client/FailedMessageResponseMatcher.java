package uk.gov.dwp.queue.triage.core.client;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

public class FailedMessageResponseMatcher extends TypeSafeMatcher<FailedMessageResponse> {

    private Matcher<FailedMessageId> failedMessageIdMatcher = new IsAnything<>();
    private Matcher<String> contentMatcher = new IsAnything<>();
    private Matcher<String> brokerMatcher = new IsAnything<>();
    private Matcher<Optional<String>> destinationMatcher = new IsAnything<>();
    private Matcher<Instant> sentAtMatcher = new IsAnything<>();
    private Matcher<Instant> failedAtMatcher = new IsAnything<>();
    private Matcher<FailedMessageStatus> statusMatcher = new IsAnything<>();
    private Matcher<Map<? extends String, ? extends Object>> propertiesMatcher = new IsAnything<>();
    private Matcher<Iterable<? extends String>> labelsMatcher = new IsAnything<>();

    private FailedMessageResponseMatcher() {
    }

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

    public FailedMessageResponseMatcher withSentAt(Instant sentAt) {
        this.sentAtMatcher = equalTo(sentAt);
        return this;
    }

    public FailedMessageResponseMatcher withSentAt(Matcher<Instant> sentAtMatcher) {
        this.sentAtMatcher = sentAtMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withFailedAt(Instant failedAt) {
        this.failedAtMatcher = equalTo(failedAt);
        return this;
    }

    public FailedMessageResponseMatcher withFailedAt(Matcher<Instant> failedAtMatcher) {
        this.failedAtMatcher = failedAtMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withStatus(FailedMessageStatus failedMessageStatus) {
        this.statusMatcher = equalTo(failedMessageStatus);
        return this;
    }

    public FailedMessageResponseMatcher withProperties(Matcher<Map<? extends String, ? extends Object>> propertiesMatcher) {
        this.propertiesMatcher = propertiesMatcher;
        return this;
    }

    public FailedMessageResponseMatcher withLabels(Matcher<Iterable<? extends String>> labelsMatcher) {
        this.labelsMatcher = labelsMatcher;
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
                && statusMatcher.matches(item.getCurrentStatus())
                && propertiesMatcher.matches(item.getProperties())
                && labelsMatcher.matches(item.getLabels());
    }

    @Override
    public void describeTo(Description description) {
        addMatcher(description, "failedMessageId is ", failedMessageIdMatcher);
        addMatcher(description, "content is ", contentMatcher);
        addMatcher(description, "broker is ", brokerMatcher);
        addMatcher(description, "destination is ", destinationMatcher);
        addMatcher(description, "sentAt is ", sentAtMatcher);
        addMatcher(description, "failedAt is ", failedAtMatcher);
        addMatcher(description, "status is", statusMatcher);
        addMatcher(description, "properties are ", propertiesMatcher);
        addMatcher(description, "labels with ", labelsMatcher);
    }

    private void addMatcher(Description description, String fieldName, Matcher<?> matcher) {
        if (!(matcher instanceof IsAnything)) {
            description.appendText(fieldName).appendDescriptionOf(matcher).appendText(" ");
        }
    }
}