package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentContainsJsonPathTest {

    private static String messageContent;
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    private ContentContainsJsonPath underTest;

    @BeforeClass
    public static void loadMessageContent() throws IOException {
        messageContent = IOUtils.toString(ContentContainsJsonPath.class.getResourceAsStream("/sample.json"), Charset.defaultCharset());
    }

    @Before
    public void setUp() {
        when(failedMessage.getContent()).thenReturn(messageContent);
    }

    @Test
    public void searchSingleFieldThatDoesNotExist() {
        underTest = new ContentContainsJsonPath("$.location");
        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void searchSingleFieldThatDoesExist() {
        underTest = new ContentContainsJsonPath("$.expensive");
        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void searchSingleFieldThatDoesExistWhereValueIsNull() {
        underTest = new ContentContainsJsonPath("$.store.bicycle.model");
        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void searchArrayForFieldThatDoesExist() {
        underTest = new ContentContainsJsonPath("$.store.book[*].author");
        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void searchArrayForFieldThatDoesExistWhereValuesAreNull() {
        underTest = new ContentContainsJsonPath("$.store.book[*].publicationDate");
        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void searchArrayForFieldThatDoesNotExist() {
        underTest = new ContentContainsJsonPath("$.store.book[*].breakfast");
        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void messageContentIsNull() {
        when(failedMessage.getContent()).thenReturn(null);
        underTest = new ContentContainsJsonPath("$.store.expensive");
        assertThat(underTest.test(failedMessage), is(false));
    }

}