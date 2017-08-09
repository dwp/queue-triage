package uk.gov.dwp.queue.triage.core.jms.spring;

import org.hamcrest.Matchers;
import org.junit.Test;

import javax.jms.Session;
import javax.jms.TextMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder.newFailedMessage;

public class FailedMessageCreatorTest {

    private final Session session = mock(Session.class);
    private final TextMessage textMessage = mock(TextMessage.class);

    @Test
    public void createJmsMessage() throws Exception {
        when(session.createTextMessage()).thenReturn(textMessage);
        FailedMessageCreator underTest = new FailedMessageCreator(newFailedMessage()
                .withContent("Hello")
                .withProperty("foo", "bar")
                .build()
        );

        assertThat(underTest.createMessage(session), Matchers.is(textMessage));

        verify(textMessage).setText("Hello");
        verify(textMessage).setObjectProperty("foo", "bar");
    }
}