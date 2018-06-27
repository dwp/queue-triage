package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StringDescriptionTest {

    private final Appendable appendable = mock(Appendable.class);
    private StringDescription underTest = new StringDescription(appendable);

    @Test
    public void instantiateDescriptionWithString() {
        underTest = new StringDescription("Something");

        assertThat(underTest.getOutput(), is("Something"));
    }

    @Test
    public void instantiateDescriptionWithAppendable() {
        when(appendable.toString()).thenReturn("Foo");

        assertThat(underTest.getOutput(), is("Foo"));
    }

    @Test
    public void appendAString() throws IOException {
        assertThat(underTest.append("Foo"), is(underTest));

        verify(appendable).append("Foo");
    }

    @Test
    public void appendAnObject() throws IOException {
        final Description returnedDescription = underTest.append(true);

        assertThat(returnedDescription, is(underTest));
        verify(appendable).append("true");
    }

    @Test
    public void appendADescription() throws IOException {
        final Description returnedDescription = underTest.append(new StringDescription("Foo"));

        assertThat(returnedDescription, is(underTest));
        verify(appendable).append("Foo");
    }

    @Test(expected = RuntimeException.class)
    public void exceptionIsThrownAsARuntimeException() throws IOException {
        when(appendable.append("Foo")).thenThrow(IOException.class);

        underTest.append("Foo");
    }
}