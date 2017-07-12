package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;

import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class DestinationMatcher extends TypeSafeMatcher<Destination> {

    private Matcher<String> brokerMatcher;
    private Matcher<Optional<? extends String>> destinationNameMatcher;

    public static DestinationMatcher aDestination() {
        return new DestinationMatcher(notNullValue(String.class), optionalWithValue(any(String.class)));
    }

    public DestinationMatcher(Matcher<String> brokerMatcher, Matcher<Optional<? extends String>> destinationNameMatcher) {
        this.brokerMatcher = brokerMatcher;
        this.destinationNameMatcher = destinationNameMatcher;
    }

    public DestinationMatcher withBrokerName(String brokerName) {
        this.brokerMatcher = equalTo(brokerName);
        return this;
    }

    public DestinationMatcher withName(String queueName) {
        this.destinationNameMatcher = optionalWithValue(equalTo(queueName));
        return this;
    }

    public DestinationMatcher withNoName() {
        this.destinationNameMatcher = equalTo(Optional.empty());
        return this;
    }

    @Override
    protected boolean matchesSafely(Destination item) {
        return brokerMatcher.matches(item.getBrokerName())
                && destinationNameMatcher.matches(item.getName());
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("brokerName is ").appendDescriptionOf(brokerMatcher)
                .appendText(" queueName is ").appendDescriptionOf(destinationNameMatcher);
    }
}
