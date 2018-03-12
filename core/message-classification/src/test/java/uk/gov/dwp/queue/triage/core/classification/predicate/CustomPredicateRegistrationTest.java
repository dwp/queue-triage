package uk.gov.dwp.queue.triage.core.classification.predicate;

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
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class CustomPredicateRegistrationTest {

    private final ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    @Before
    public void setUp() {
        objectMapper.registerSubtypes(BooleanPredicate.class);
    }

    @Test
    public void canSerialiseACustomPredicate() throws JsonProcessingException {
        final String json = objectMapper.writeValueAsString(new BooleanPredicate(true));

        assertThat(json, allOf(
                hasJsonPath("$._type", equalTo("boolean")),
                hasJsonPath("$.result", equalTo(true))
        ));

    }

    @Test
    public void canDeserialiseACustomPredicate() throws IOException {
        FailedMessagePredicate underTest = objectMapper.readValue("{ \"_type\": \"boolean\", \"result\": true }", FailedMessagePredicate.class);
        assertThat(underTest.getClass(), equalTo(BooleanPredicate.class));
        assertThat(underTest.test(failedMessage), is(true));
    }

    @JsonTypeName("boolean")
    public static class BooleanPredicate implements FailedMessagePredicate {

        @JsonProperty
        private final boolean result;

        public BooleanPredicate(@JsonProperty("result") boolean result) {
            this.result = result;
        }

        @Override
        public boolean test(FailedMessage failedMessage) {
            return result;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            return object instanceof BooleanPredicate;
        }
    }
}
