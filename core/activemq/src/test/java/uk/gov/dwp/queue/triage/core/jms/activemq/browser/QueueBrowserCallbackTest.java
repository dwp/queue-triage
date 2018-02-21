package uk.gov.dwp.queue.triage.core.jms.activemq.browser;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;

import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Enumeration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class QueueBrowserCallbackTest {

    private final FailedMessageListener failedMessageListener = mock(FailedMessageListener.class);
    private final QueueBrowser queueBrowser = mock(QueueBrowser.class);
    private final Session session = mock(Session.class);

    private final QueueBrowserCallback underTest =
            new QueueBrowserCallback(failedMessageListener);
    private final Enumeration enumeration = mock(Enumeration.class);
    private final TextMessage textMessage2 = mock(TextMessage.class);
    private final TextMessage textMessage1 = mock(TextMessage.class);

    @Test
    public void noMessagesToBrowse() throws JMSException {
        when(queueBrowser.getEnumeration()).thenReturn(enumeration);
        when(enumeration.hasMoreElements()).thenReturn(false);

        assertThat(underTest.doInJms(session, queueBrowser), is(0L));
        verifyZeroInteractions(failedMessageListener);
    }

    @Test
    public void messagesToBrowse() throws JMSException {
        when(queueBrowser.getEnumeration()).thenReturn(enumeration);
        when(enumeration.hasMoreElements()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(enumeration.nextElement()).thenReturn(textMessage1).thenReturn(textMessage2);

        assertThat(underTest.doInJms(session, queueBrowser), is(2L));
        verify(failedMessageListener).onMessage(textMessage1);
        verify(failedMessageListener).onMessage(textMessage2);
        verifyZeroInteractions(failedMessageListener);
    }

}