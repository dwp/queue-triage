package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrokerEqualsPredicateTest extends AbstractFailedMessagePredicateTest {

    private final Destination destination = mock(Destination.class);

    @Before
    public void setUp() {
        when(failedMessage.getDestination()).thenReturn(destination);
    }

    @Test(expected = NullPointerException.class)
    public void nameOfBrokerCannotBeNull() {
        underTest = new BrokerEqualsPredicate(null);
    }

    @Test
    public void nameOfBrokerMatchers() {
        when(destination.getBrokerName()).thenReturn("some-broker");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void nameOfBrokerDoesNotMatch() {
        when(destination.getBrokerName()).thenReturn("another-broker");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Override
    protected Matcher<String> expectedDescription() {
        return is("broker = 'some-broker'");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new BrokerEqualsPredicate("some-broker");
    }
}