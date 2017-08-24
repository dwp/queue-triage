package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrokerEqualsPredicateTest {

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final Destination destination = mock(Destination.class);

    private BrokerEqualsPredicate underTest;

    @Before
    public void setUp() {
        when(failedMessage.getDestination()).thenReturn(destination);
    }

    @Test(expected = NullPointerException.class)
    public void nameOfBrokerCannotBeNull() throws Exception {
        underTest = new BrokerEqualsPredicate(null);
    }

    @Test
    public void nameOfBrokerMatchers() throws Exception {
        underTest = new BrokerEqualsPredicate("some-broker");
        when(destination.getBrokerName()).thenReturn("some-broker");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void nameOfBrokerDoesNotMatch() throws Exception {
        underTest = new BrokerEqualsPredicate("some-broker");
        when(destination.getBrokerName()).thenReturn("another-broker");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

        underTest = new BrokerEqualsPredicate("some-broker");
        String json = objectMapper.writeValueAsString(underTest);

        assertThat(EqualsBuilder.reflectionEquals(
                underTest,
                objectMapper.readValue(json, BrokerEqualsPredicate.class)
        ), is(true));
    }

}