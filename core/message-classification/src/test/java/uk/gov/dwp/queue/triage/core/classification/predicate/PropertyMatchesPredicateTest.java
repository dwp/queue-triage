package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.matchers.ReflectionEqualsMatcher.reflectionEquals;

public class PropertyMatchesPredicateTest {

    private FailedMessage failedMessage = mock(FailedMessage.class);

    private PropertyMatchesPredicate underTest;

    @Test(expected = IllegalArgumentException.class)
    public void propertyNameCannotBeNull() throws Exception {
        underTest = new PropertyMatchesPredicate(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void regexCannotBeNull() {
        underTest = new PropertyMatchesPredicate("foo", null);
    }


    @Test
    public void propertyMatchesPattern() throws Exception {
        when(failedMessage.getProperty("foo")).thenReturn("some.property.value");

        underTest = new PropertyMatchesPredicate("foo", "^some\\.property.*");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void propertyDoesNotMatchPattern() throws Exception {
        when(failedMessage.getProperty("foo")).thenReturn("some.property.value");

        underTest = new PropertyMatchesPredicate("foo", "^some\\.property");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void propertyValueIsNull() throws Exception {
        when(failedMessage.getProperty("foo")).thenReturn(null);

        underTest = new PropertyMatchesPredicate("foo", "^some\\.property");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void propertyIsABoolean() {
        when(failedMessage.getProperty("aBoolean")).thenReturn(true);

        underTest = new PropertyMatchesPredicate("aBoolean", "true");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void propertyIsANumber() {
        when(failedMessage.getProperty("aNumber")).thenReturn(12345);

        underTest = new PropertyMatchesPredicate("aNumber", "^12345$");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

        underTest = new PropertyMatchesPredicate("foo", "bar");
        String json = objectMapper.writeValueAsString(underTest);

        assertThat(objectMapper.readValue(json, FailedMessagePredicate.class), reflectionEquals(underTest, "pattern"));
    }


}