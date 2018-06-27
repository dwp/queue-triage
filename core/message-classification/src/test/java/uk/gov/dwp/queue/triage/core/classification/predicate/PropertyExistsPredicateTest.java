package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class PropertyExistsPredicateTest extends AbstractFailedMessagePredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void propertyCannotBeNull() {
        underTest = new PropertyExistsPredicate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void propertyCannotBeBlank() {
        underTest = new PropertyExistsPredicate(" ");
    }

    @Test
    public void propertyExists() {
        when(failedMessage.getProperties()).thenReturn(Collections.singletonMap("foo", "bar"));

        assertThat(underTest.test(failedMessage),  is(true));
    }

    @Test
    public void propertyDoesNotExist() {
        when(failedMessage.getProperties()).thenReturn(Collections.emptyMap());

        assertThat(underTest.test(failedMessage),  is(false));
    }

    @Override
    protected Matcher<String> expectedDescription() {
        return is("property[foo] exists");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new PropertyExistsPredicate("foo");
    }
}