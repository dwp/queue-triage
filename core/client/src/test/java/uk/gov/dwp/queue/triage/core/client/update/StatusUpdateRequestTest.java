package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;

public class StatusUpdateRequestTest {

    private static ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper();

    @Test
    public void serialiseAndDeserialiseRequest() throws IOException {
        String json = OBJECT_MAPPER.writeValueAsString(new StatusUpdateRequest(FAILED));

        assertThat(json, allOf(
                hasJsonPath("$._type", equalTo("status")),
                hasJsonPath("$.status", equalTo(FAILED.toString()))
        ));

        assertThat(OBJECT_MAPPER.readValue(json, StatusUpdateRequest.class), aStatusUpdateRequest(equalTo(FAILED)));
    }

    private TypeSafeMatcher<StatusUpdateRequest> aStatusUpdateRequest(Matcher<FailedMessageStatus> statusMatcher) {
        return new TypeSafeMatcher<StatusUpdateRequest>() {
            @Override
            protected boolean matchesSafely(StatusUpdateRequest item) {
                return statusMatcher.matches(item.getStatus());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("status is ").appendDescriptionOf(statusMatcher);
            }
        };
    }
}