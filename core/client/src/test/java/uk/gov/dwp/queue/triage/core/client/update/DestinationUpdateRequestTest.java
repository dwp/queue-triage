package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class DestinationUpdateRequestTest {

    private static final String BROKER_NAME = "some-broker";
    private static final String DESTINATION_NAME = "some-queue";
    private static ObjectMapper OBJECT_MAPPER = JacksonConfiguration.defaultObjectMapper();

    @Test
    public void serialiseAndDeserialiseRequest() throws IOException {
        String json = OBJECT_MAPPER.writeValueAsString(new DestinationUpdateRequest(BROKER_NAME, DESTINATION_NAME));

        assertThat(json, allOf(
                hasJsonPath("$._type", equalTo("destination")),
                hasJsonPath("$.broker", equalTo(BROKER_NAME)),
                hasJsonPath("$.destination", equalTo(DESTINATION_NAME))
        ));

        assertThat(OBJECT_MAPPER.readValue(json, DestinationUpdateRequest.class), aDestinationUpdateRequest(Matchers.equalTo(BROKER_NAME), Matchers.equalTo(DESTINATION_NAME)));
    }

    private TypeSafeMatcher<DestinationUpdateRequest> aDestinationUpdateRequest(Matcher<String> brokerMatcher,
                                                                                Matcher<String> destinationMatcher) {
        return new TypeSafeMatcher<DestinationUpdateRequest>() {
            @Override
            protected boolean matchesSafely(DestinationUpdateRequest item) {
                return brokerMatcher.matches(item.getBroker()) &&
                        destinationMatcher.matches(item.getDestination());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("broker is ").appendDescriptionOf(brokerMatcher)
                        .appendText(" destination is ").appendDescriptionOf(destinationMatcher);
            }
        };
    }
}