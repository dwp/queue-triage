package uk.gov.dwp.queue.triage.core.classification.predicate;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class ContentMatchesJsonPathTest extends AbstractFailedMessagePredicateTest {

    private static String messageContent;

    @BeforeClass
    public static void loadMessageContent() throws IOException {
        messageContent = IOUtils.toString(ContentMatchesJsonPath.class.getResourceAsStream("/sample.json"), Charset.defaultCharset());
    }

    @Before
    public void setUp() {
        when(failedMessage.getContent()).thenReturn(messageContent);
    }

    @Test
    public void attemptToMatchSingleFieldThatDoesNotExist() {
        underTest = new ContentMatchesJsonPath("$.location", "true");
        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void attemptToMatchSingleFieldRegexDoesNotMatch() {
        underTest = new ContentMatchesJsonPath("$.expensive", "false");
        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void attemptToMatchSingleFieldValueIsNull() {
        underTest = new ContentMatchesJsonPath("$.store.bicycle.model", "^foo$");
        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void successfullyMatchSingleField() {
        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void attemptToMatchSingleValueInArrayForFieldThatDoesExist() {
        underTest = new ContentMatchesJsonPath("$.store.book[0].isbn", "0-553-21311-3");
        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void attemptToMatchSingleValueInArrayWhereValuesAreNull() {
        underTest = new ContentMatchesJsonPath("$.store.book[0].publicationDate", "2018-01-01");
        assertThat(underTest.test(failedMessage), is(false));
    }

    @Test
    public void successfullyMatchSingleValueInArray() {
        underTest = new ContentMatchesJsonPath("$.store.book[1].category", "fiction");
        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void attemptToMatchAnyValueInArrayForFieldThatDoesNotExist() {
        underTest = new ContentMatchesJsonPath("$.store.book[*].something", "");
        assertThat("", underTest.test(failedMessage), is(false));
    }

    @Test
    public void attemptToMatchAnyValueInArray() {
        underTest = new ContentMatchesJsonPath("$.store.book[*].category", "romance");
        assertThat("No books should exist with category 'romance'", underTest.test(failedMessage), is(false));
    }

    @Test
    public void successfullyMatchAntValueInArray() {
        underTest = new ContentMatchesJsonPath("$.store.book[*].category", "fiction");
        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void messageContentIsNull() {
        when(failedMessage.getContent()).thenReturn(null);

        assertThat(underTest.test(failedMessage), is(false));
    }

    @Override
    protected Matcher<String> expectedDescription() {
        return is("$.expensive matches 10");
    }

    @Override
    protected FailedMessagePredicate createPredicateUnderTest() {
        return new ContentMatchesJsonPath("$.expensive", "10");
    }

    @Override
    protected String[] excludedFieldsFromEquality() {
        return new String[]{"parseContext", "pattern"};
    }
}