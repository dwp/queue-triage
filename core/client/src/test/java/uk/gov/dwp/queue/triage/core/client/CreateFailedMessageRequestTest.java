package uk.gov.dwp.queue.triage.core.client;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.core.IsAnything;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.CreateFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;

public class CreateFailedMessageRequestTest {

    private static final Instant NOW = Instant.now();
    private static final String BROKER_NAME = "some-broker";
    private static final String DESTINATION_NAME = "some-destination";
    private static final String MESSAGE_CONTENT = "content";

    private final CreateFailedMessageRequestBuilder createFailedMessageRequestBuilder = newCreateFailedMessageRequest()
            .withContent(MESSAGE_CONTENT)
            .withBrokerName(BROKER_NAME)
            .withDestinationName(DESTINATION_NAME)
            .withFailedDateTime(NOW)
            .withSentDateTime(NOW);

    @Test
    public void attemptingToSetNullPropertiesCreatesAnEmptyMap() {
        final CreateFailedMessageRequest createFailedMessageRequest = createFailedMessageRequestBuilder
                .withProperties(null)
                .build();

        assertThat(createFailedMessageRequest, is(createFailedMessageRequest().withProperties(equalTo(emptyMap()))));
    }

    @Test
    public void attemptingToSetNullLabelsCreatesAnEmptySet() {
        final CreateFailedMessageRequest createFailedMessageRequest = createFailedMessageRequestBuilder
                .withLabels(null)
                .build();

        assertThat(createFailedMessageRequest, is(createFailedMessageRequest().withLabels(emptyIterable())));
    }

    @Test
    public void addProperties() {
        final CreateFailedMessageRequest createFailedMessageRequest = createFailedMessageRequestBuilder
                .withProperties(Collections.singletonMap("foo", "bar"))
                .withProperty("ham", "eggs")
                .build();

        assertThat(createFailedMessageRequest, is(createFailedMessageRequest()
                .withProperties(allOf(Matchers.hasEntry("foo", "bar"), Matchers.hasEntry("ham", "eggs")))));
    }

    @Test
    public void addLabels() {
        final CreateFailedMessageRequest createFailedMessageRequest = createFailedMessageRequestBuilder
                .withLabels(Collections.singleton("foo"))
                .withLabel("bar")
                .build();

        assertThat(createFailedMessageRequest, is(createFailedMessageRequest()
                .withLabels(containsInAnyOrder("foo", "bar"))));
    }

    public static CreateFailedMessageRequestMatcher createFailedMessageRequest() {
        return new CreateFailedMessageRequestMatcher()
                .withBrokerName(equalTo(BROKER_NAME))
                .withDestinationName(equalTo(DESTINATION_NAME))
                .withContent(equalTo(MESSAGE_CONTENT))
                .withFailedAt(equalTo(NOW))
                .withSentAt(equalTo(NOW));
    }

    private static class CreateFailedMessageRequestMatcher extends TypeSafeMatcher<CreateFailedMessageRequest> {
        private Matcher<FailedMessageId> failedMessageIdMatcher = new IsAnything<>();
        private Matcher<String> contentMatcher = new IsAnything<>();
        private Matcher<String> brokerNameMatcher = new IsAnything<>();
        private Matcher<String> destinationNameMatcher = new IsAnything<>();
        private Matcher<Instant> sentAtMatcher = new IsAnything<>();
        private Matcher<Instant> failedAtMatcher = new IsAnything<>();
        private Matcher<Map<? extends String, ? extends Object>> propertiesMatcher = equalTo(new HashMap<>());
        private Matcher<Iterable<? extends String>> labelsMatcher = new IsEmptyIterable<>();

        @Override
        protected boolean matchesSafely(CreateFailedMessageRequest item) {
            return failedMessageIdMatcher.matches(item.getFailedMessageId())
                    && contentMatcher.matches(item.getContent())
                    && brokerNameMatcher.matches(item.getBrokerName())
                    && destinationNameMatcher.matches(item.getDestinationName())
                    && sentAtMatcher.matches(item.getSentAt())
                    && failedAtMatcher.matches(item.getFailedAt())
                    && propertiesMatcher.matches(item.getProperties())
                    && labelsMatcher.matches(item.getLabels())
                    ;
        }

        @Override
        public void describeTo(Description description) {
            description
                    .appendText("failedMessageId is ").appendDescriptionOf(failedMessageIdMatcher)
                    .appendText(" content is ").appendDescriptionOf(contentMatcher)
                    .appendText(" broker is ").appendDescriptionOf(brokerNameMatcher)
                    .appendText(" destination is ").appendDescriptionOf(destinationNameMatcher)
                    .appendText(" sentAt is ").appendDescriptionOf(sentAtMatcher)
                    .appendText(" failedAt is ").appendDescriptionOf(failedAtMatcher)
                    .appendText(" properties are ").appendDescriptionOf(propertiesMatcher)
                    .appendText(" labels are ").appendDescriptionOf(labelsMatcher)
            ;
        }

        public CreateFailedMessageRequestMatcher withFailedMessageId(Matcher<FailedMessageId> failedMessageIdMatcher) {
            this.failedMessageIdMatcher = failedMessageIdMatcher;
            return this;
        }

        public CreateFailedMessageRequestMatcher withContent(Matcher<String> contentMatcher) {
            this.contentMatcher = contentMatcher;
            return this;
        }

        public CreateFailedMessageRequestMatcher withBrokerName(Matcher<String> brokerNameMatcher) {
            this.brokerNameMatcher = brokerNameMatcher;
            return this;
        }

        public CreateFailedMessageRequestMatcher withDestinationName(Matcher<String> destinationNameMatcher) {
            this.destinationNameMatcher = destinationNameMatcher;
            return this;
        }

        public CreateFailedMessageRequestMatcher withSentAt(Matcher<Instant> sentAtMatcher) {
            this.sentAtMatcher = sentAtMatcher;
            return this;
        }

        public CreateFailedMessageRequestMatcher withFailedAt(Matcher<Instant> failedAtMatcher) {
            this.failedAtMatcher = failedAtMatcher;
            return this;
        }

        public CreateFailedMessageRequestMatcher withProperties(Matcher<Map<? extends String, ? extends Object>> propertiesMatcher) {
            this.propertiesMatcher = propertiesMatcher;
            return this;
        }

        public CreateFailedMessageRequestMatcher withLabels(Matcher<Iterable<? extends String>> labelsMatcher) {
            this.labelsMatcher = labelsMatcher;
            return this;
        }
    }
}