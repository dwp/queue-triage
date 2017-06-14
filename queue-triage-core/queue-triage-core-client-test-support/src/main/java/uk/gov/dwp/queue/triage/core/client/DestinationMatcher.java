package uk.gov.dwp.queue.triage.core.client;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class DestinationMatcher extends TypeSafeMatcher<Destination> {

    private Matcher<String> brokerMatcher;
    private Matcher<String> destinationNameMatcher;

    public static DestinationMatcher aDestination() {
        return new DestinationMatcher(notNullValue(String.class), notNullValue(String.class));
    }

    public DestinationMatcher(Matcher<String> brokerMatcher, Matcher<String> destinationNameMatcher) {
        this.brokerMatcher = brokerMatcher;
        this.destinationNameMatcher = destinationNameMatcher;
    }

    public DestinationMatcher withBrokerName(String brokerName) {
        this.brokerMatcher = equalTo(brokerName);
        return this;
    }

    public DestinationMatcher withName(String queueName) {
        this.destinationNameMatcher = equalTo(queueName);
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
