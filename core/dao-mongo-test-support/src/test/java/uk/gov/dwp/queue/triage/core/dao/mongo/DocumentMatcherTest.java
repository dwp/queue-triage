package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import org.hamcrest.StringDescription;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DocumentMatcherTest {

    private final DocumentMatcher underTest = DocumentMatcher.hasField("foo", equalTo("bar"));

    @Test
    public void documentMatchesSuccessfully() {
        assertTrue(underTest.matches(new Document("foo", "bar")));
    }

    @Test
    public void documentDoesNotMatch() {
        assertFalse(underTest.matches(new Document("ham", "eggs")));
    }

    @Test
    public void objectOfIncorrectTypeDoesNotMatch() {
        assertFalse(underTest.matches(Collections.singletonMap("foo", "bar")));
    }

    @Test
    public void nullObjectDoesNotMatch() {
        assertFalse(underTest.matches(null));
    }

    @Test
    public void describeToAppendsFieldNameAndMatcherDescription() {
        final StringDescription description = new StringDescription();

        underTest.describeTo(description);

        assertEquals("foo is <\"bar\">", description.toString());
    }
}