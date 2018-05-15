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

public class ContentUpdateRequestTest {

    private static final String CONTENT = "some-content";
    private static ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper();

    @Test
    public void serialiseAndDeserialiseRequest() throws IOException {
        String json = OBJECT_MAPPER.writeValueAsString(new ContentUpdateRequest(CONTENT));

        assertThat(json, allOf(
                hasJsonPath("$._type", equalTo("content")),
                hasJsonPath("$.content", equalTo(CONTENT))
        ));

        assertThat(OBJECT_MAPPER.readValue(json, ContentUpdateRequest.class), aContentUpdateRequest(Matchers.equalTo(CONTENT)));
    }

    private TypeSafeMatcher<ContentUpdateRequest> aContentUpdateRequest(Matcher<String> contentMatcher) {
        return new TypeSafeMatcher<ContentUpdateRequest>() {
            @Override
            protected boolean matchesSafely(ContentUpdateRequest item) {
                return contentMatcher.matches(item.getContent());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("content is ").appendDescriptionOf(contentMatcher);
            }
        };
    }
}