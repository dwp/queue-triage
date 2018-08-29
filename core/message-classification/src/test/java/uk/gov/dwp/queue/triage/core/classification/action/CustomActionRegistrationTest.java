package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class CustomActionRegistrationTest {

    private final ObjectMapper objectMapper = JacksonConfiguration.defaultObjectMapper();
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    @Before
    public void setUp() {
        objectMapper.registerSubtypes(GetPropertyAction.class);
    }

    @Test
    public void canSerialiseACustomPredicate() throws JsonProcessingException {
        final String json = objectMapper.writeValueAsString(new GetPropertyAction("foo"));

        assertThat(json, allOf(
                hasJsonPath("$._action", equalTo("getProperty")),
                hasJsonPath("$.propertyName", equalTo("foo"))
        ));
    }

    @Test
    public void canDeserialiseACustomPredicate() throws IOException {
        FailedMessageAction underTest = objectMapper.readValue("{ \"_action\": \"getProperty\", \"propertyName\": \"foo\" }", FailedMessageAction.class);
        assertThat(underTest.getClass(), equalTo(GetPropertyAction.class));

        underTest.accept(failedMessage);

        verify(failedMessage).getProperty("foo");
    }

    @JsonTypeName("getProperty")
    public static class GetPropertyAction implements FailedMessageAction {

        @JsonProperty
        private final String propertyName;

        public GetPropertyAction(@JsonProperty("propertyName") String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public void accept(FailedMessage failedMessage) {
            failedMessage.getProperty(propertyName);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            return object instanceof GetPropertyAction;
        }
    }

}
