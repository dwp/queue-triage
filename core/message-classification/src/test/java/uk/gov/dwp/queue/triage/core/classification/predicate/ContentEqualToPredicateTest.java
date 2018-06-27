package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.hamcrest.Matcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentEqualToPredicateTest extends AbstractFailedMessagePredicateTest {

    private FailedMessage failedMessage = mock(FailedMessage.class);

    @Test(expected = IllegalArgumentException.class)
    public void propertyNameCannotBeNull() {
        underTest = new ContentEqualToPredicate(null);
    }

    @Test
    public void contentInMessageIsNull() {
        when(failedMessage.getContent()).thenReturn(null);

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void contentDoesNotMatch() {
        when(failedMessage.getContent()).thenReturn("bar");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void contentMatches() {
        when(failedMessage.getContent()).thenReturn("foo");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Override
    protected Matcher<String> expectedDescription() {
        return is("content = foo");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new ContentEqualToPredicate("foo");
    }
}