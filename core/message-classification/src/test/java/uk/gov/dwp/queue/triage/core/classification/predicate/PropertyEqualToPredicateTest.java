package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropertyEqualToPredicateTest {

    private FailedMessage failedMessage = mock(FailedMessage.class);
    private PropertyEqualToPredicate underTest;

    @Test(expected = IllegalArgumentException.class)
    public void propertyNameCannotBeNull() throws Exception {
        underTest = new PropertyEqualToPredicate(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void expectedValueCannotBeNull() {
        underTest = new PropertyEqualToPredicate("foo", null);
    }

    @Test
    public void propertiesMatch() {
        when(failedMessage.getProperty("foo")).thenReturn("bar");
        underTest = new PropertyEqualToPredicate("foo", "bar");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void propertiesDoNotMatch() {
        when(failedMessage.getProperty("foo")).thenReturn("rab");
        underTest = new PropertyEqualToPredicate("foo", "bar");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void propertyDoesNotExist() throws Exception {
        when(failedMessage.getProperty("foo")).thenReturn(null);

        underTest = new PropertyEqualToPredicate("foo", "bar");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

        underTest = new PropertyEqualToPredicate("foo", "bar");
        String json = objectMapper.writeValueAsString(underTest);

        assertThat(EqualsBuilder.reflectionEquals(
                underTest,
                objectMapper.readValue(json, FailedMessagePredicate.class)
        ), is(true));
    }
}