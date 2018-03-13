package uk.gov.dwp.queue.triage.web.server.api;

import org.junit.Test;
import uk.gov.dwp.queue.triage.web.server.api.LabelExtractor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;

public class LabelExtractorTest {

    private final LabelExtractor underTest = new LabelExtractor();

    @Test
    public void emptyStingReturnsEmptySet() throws Exception {
        assertThat(underTest.extractLabels(""), is(emptyIterable()));
    }

    @Test
    public void stringContainingWhitespaceReturnsEmptySet() throws Exception {
        assertThat(underTest.extractLabels(" \t"), is(emptyIterable()));
    }

    @Test
    public void stringContainingOnlyCommasWhitespaceReturnsEmptySet() throws Exception {
        assertThat(underTest.extractLabels(" ,,\t,"), is(emptyIterable()));
    }

    @Test
    public void leadingAndTrailingWhitespaceIsRemovedFromLabels() {
        assertThat(underTest.extractLabels(" foo , bar\t"), containsInAnyOrder("foo", "bar"));
    }
}