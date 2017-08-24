package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DestinationEqualsPredicateTest {

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final Destination destination = mock(Destination.class);

    private DestinationEqualsPredicate underTest;

    @Before
    public void setUp() {
        when(failedMessage.getDestination()).thenReturn(destination);
    }

    @Test(expected = NullPointerException.class)
    public void destinationCannotBeNull() throws Exception {
        underTest = new DestinationEqualsPredicate(null);
    }

    @Test
    public void nameOfDestinationMatches() throws Exception {
        when(destination.getName()).thenReturn(Optional.of("some-destination"));

        underTest = new DestinationEqualsPredicate(Optional.of("some-destination"));

        assertThat(underTest.test(failedMessage), Matchers.is(true));
    }

    @Test
    public void nameOfDestinationIsEmpty() throws Exception {
        when(destination.getName()).thenReturn(Optional.empty());

        underTest = new DestinationEqualsPredicate(Optional.of("some-destination"));

        assertThat(underTest.test(failedMessage), Matchers.is(false));
    }

    @Test
    public void nameOfDestinationIDoesNotMatch() throws Exception {
        when(destination.getName()).thenReturn(Optional.of("another-destination"));

        underTest = new DestinationEqualsPredicate(Optional.of("some-destination"));

        assertThat(underTest.test(failedMessage), Matchers.is(false));
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

        underTest = new DestinationEqualsPredicate(Optional.of("destination-name"));
        String json = objectMapper.writeValueAsString(underTest);

        assertThat(EqualsBuilder.reflectionEquals(
                underTest,
                objectMapper.readValue(json, DestinationEqualsPredicate.class)
        ), is(true));
    }


}