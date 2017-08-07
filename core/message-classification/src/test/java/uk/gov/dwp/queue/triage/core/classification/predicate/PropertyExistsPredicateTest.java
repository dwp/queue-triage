package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropertyExistsPredicateTest {

    private FailedMessage failedMessage = mock(FailedMessage.class);
    private PropertyExistsPredicate underTest;

    @Test(expected = IllegalArgumentException.class)
    public void propertyCannotBeNull() throws Exception {
        underTest = new PropertyExistsPredicate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void propertyCannotBeBlank() {
        underTest = new PropertyExistsPredicate(" ");
    }

    @Test
    public void propertyExists() throws Exception {
        when(failedMessage.getProperties()).thenReturn(Collections.singletonMap("foo", "bar"));

        underTest = new PropertyExistsPredicate("foo");

        assertThat(underTest.test(failedMessage),  is(true));
    }

    @Test
    public void propertyDoesNotExist() throws Exception {
        when(failedMessage.getProperties()).thenReturn(Collections.emptyMap());

        underTest = new PropertyExistsPredicate("foo");

        assertThat(underTest.test(failedMessage),  is(false));
    }
}