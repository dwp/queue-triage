package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DestinationEqualsPredicateTest extends AbstractFailedMessagePredicateTest {

    private final Destination destination = mock(Destination.class);

    @Before
    public void setUp() {
        when(failedMessage.getDestination()).thenReturn(destination);
    }

    @Test(expected = NullPointerException.class)
    public void destinationCannotBeNull() {
        underTest = new DestinationEqualsPredicate(null);
    }

    @Test
    public void nameOfDestinationMatches() {
        when(destination.getName()).thenReturn(Optional.of("some-destination"));

        assertThat(underTest.test(failedMessage), Matchers.is(true));
    }

    @Test
    public void nameOfDestinationIsEmpty() {
        when(destination.getName()).thenReturn(Optional.empty());

        assertThat(underTest.test(failedMessage), Matchers.is(false));
    }

    @Test
    public void nameOfDestinationIDoesNotMatch() {
        when(destination.getName()).thenReturn(Optional.of("another-destination"));

        assertThat(underTest.test(failedMessage), Matchers.is(false));
    }

    @Test
    public void describeWithAnEmptyDestination() {
        underTest = new DestinationEqualsPredicate(Optional.empty());

        final Description<String> description = underTest.describe(new StringDescription());

        assertThat(description.getOutput(), is("destination is empty"));
    }

    @Override
    protected Matcher<String> expectedDescription() {
        return is("destination = 'some-destination'");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new DestinationEqualsPredicate(Optional.of("some-destination"));
    }
}