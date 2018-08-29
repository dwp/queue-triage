package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class PropertyMatchesPredicateTest extends AbstractFailedMessagePredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void propertyNameCannotBeNull() {
        underTest = new PropertyMatchesPredicate(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void regexCannotBeNull() {
        underTest = new PropertyMatchesPredicate("foo", null);
    }


    @Test
    public void propertyMatchesPattern() {
        when(failedMessage.getProperty("foo")).thenReturn("some.property.value");

        underTest = new PropertyMatchesPredicate("foo", "^some\\.property.*");

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void propertyDoesNotMatchPattern() {
        when(failedMessage.getProperty("foo")).thenReturn("some.property.value");

        underTest = new PropertyMatchesPredicate("foo", "^some\\.property");

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void propertyValueIsNull() {
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

    @Override
    protected Matcher<String> expectedDescription() {
        return is("property[foo] matches bar");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new PropertyMatchesPredicate("foo", "bar");
    }

    @Override
    protected String[] excludedFieldsFromEquality() {
        return new String[] {"pattern"};
    }
}