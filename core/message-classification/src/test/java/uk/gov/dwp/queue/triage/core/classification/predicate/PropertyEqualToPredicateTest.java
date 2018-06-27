package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class PropertyEqualToPredicateTest extends AbstractFailedMessagePredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void propertyNameCannotBeNull() {
        underTest = new PropertyEqualToPredicate(null, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void expectedValueCannotBeNull() {
        underTest = new PropertyEqualToPredicate("foo", null);
    }

    @Test
    public void propertiesMatch() {
        when(failedMessage.getProperty("foo")).thenReturn("bar");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void propertiesDoNotMatch() {
        when(failedMessage.getProperty("foo")).thenReturn("rab");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void propertyDoesNotExist() {
        when(failedMessage.getProperty("foo")).thenReturn(null);

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Override
    protected Matcher<String> expectedDescription() {
        return is("property[foo] = bar");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new PropertyEqualToPredicate("foo", "bar");
    }
}