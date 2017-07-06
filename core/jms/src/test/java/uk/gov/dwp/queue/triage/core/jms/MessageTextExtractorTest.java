package uk.gov.dwp.queue.triage.core.jms;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.jms.BytesMessage;
import javax.jms.TextMessage;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageTextExtractorTest {

    private final MessageTextExtractor underTest = new MessageTextExtractor();

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void extractTextFromMessage() throws Exception {
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn("Text");
        assertThat(underTest.extractText(textMessage), is("Text"));
    }

    @Test
    public void throwsExceptionIfNotTextMessage() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(allOf(startsWith("Expected TextMessage received:"), containsString("javax.jms.BytesMessage")));

        underTest.extractText(mock(BytesMessage.class));

        // Verify Logging?
    }



}