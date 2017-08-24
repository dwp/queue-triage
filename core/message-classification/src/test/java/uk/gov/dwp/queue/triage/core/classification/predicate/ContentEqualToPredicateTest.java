package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentEqualToPredicateTest {

    private FailedMessage failedMessage = mock(FailedMessage.class);
    private ContentEqualToPredicate underTest;

    @Test(expected = IllegalArgumentException.class)
    public void propertyNameCannotBeNull() throws Exception {
        underTest = new ContentEqualToPredicate(null);
    }

    @Test
    public void contentInMessageIsNull() {
        when(failedMessage.getContent()).thenReturn(null);
        underTest = new ContentEqualToPredicate("foo");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void contentDoesNotMatch() {
        when(failedMessage.getContent()).thenReturn("bar");
        underTest = new ContentEqualToPredicate("foo");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void contentMatches() throws Exception {
        when(failedMessage.getContent()).thenReturn("foo");

        underTest = new ContentEqualToPredicate("foo");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

        underTest = new ContentEqualToPredicate("foo");
        String json = objectMapper.writeValueAsString(underTest);

        assertThat(reflectionEquals(
                underTest,
                objectMapper.readValue(json, FailedMessagePredicate.class)
        ), is(true));
    }

}