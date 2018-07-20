package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.hamcrest.Matcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.predicate.AbstractFailedMessagePredicateTest;
import uk.gov.dwp.queue.triage.core.classification.predicate.BooleanPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class FailedMessagePredicateWithResultTest extends AbstractFailedMessagePredicateTest {

    private final FailedMessagePredicate failedMessagePredicate = mock(FailedMessagePredicate.class);
    private final FailedMessagePredicateWithResult underTest = new FailedMessagePredicateWithResult(true, failedMessagePredicate);

    @Test
    public void testEquals() {
        assertThat(underTest.equals(null), is(false));
        assertThat(underTest.equals(new BooleanPredicate(true)), is(false));
        assertThat(underTest.equals(new FailedMessagePredicateWithResult(false, failedMessagePredicate)), is(false));
        assertThat(underTest.equals(new FailedMessagePredicateWithResult(true, mock(FailedMessagePredicate.class))), is(false));
        assertThat(underTest.equals(new FailedMessagePredicateWithResult(true, failedMessagePredicate)), is(true));
    }

    @Test
    public void testHashCode() {
        assertThat(underTest.hashCode(), is(Arrays.hashCode(new Object[] {true, failedMessagePredicate})));
    }

    @Override
    protected Matcher<String> expectedDescription() {
        return equalTo("true [true]");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new FailedMessagePredicateWithResult(true, new BooleanPredicate(true));
    }
}