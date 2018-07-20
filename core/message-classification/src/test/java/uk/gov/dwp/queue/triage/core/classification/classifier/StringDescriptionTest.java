package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

public class StringDescriptionTest {

    private StringDescription underTest;

    @Test
    public void instantiateAnEmptyStringDescription() {
        underTest = new StringDescription();

        assertThat(underTest.getOutput(), is(""));
        assertThat(underTest.toString(), is(""));
    }

    @Test
    public void instantiateDescriptionWithString() {
        underTest = new StringDescription("Foo");

        assertThat(underTest.getOutput(), is("Foo"));
        assertThat(underTest.toString(), is("Foo"));
    }

    @Test
    public void appendAStringReturnsANewObject() {
        underTest = new StringDescription("Foo");
        final Description<String> returnedDescription = underTest.append(" Bar");

        assertThat(returnedDescription, is(not(sameInstance(underTest))));
        assertThat(returnedDescription.getOutput(), is("Foo Bar"));
        assertThat(underTest.toString(), is("Foo"));
    }

    @Test
    public void appendAnObject()  {
        underTest = new StringDescription("Foo ");

        final Description returnedDescription = underTest.append(true);

        assertThat(returnedDescription, is(not(sameInstance(underTest))));
        assertThat(returnedDescription.getOutput(), is("Foo true"));
        assertThat(underTest.getOutput(), is("Foo "));
    }

    @Test
    public void appendADescription() {
        underTest = new StringDescription("Foo");
        final StringDescription anotherDescription = new StringDescription(" Bar");

        final Description returnedDescription = underTest.append(anotherDescription);

        assertThat(returnedDescription, is(not(sameInstance(underTest))));
        assertThat(returnedDescription.getOutput(), is("Foo Bar"));
        assertThat(underTest.getOutput(), is("Foo"));
        assertThat(anotherDescription.getOutput(), is(" Bar"));
    }
}