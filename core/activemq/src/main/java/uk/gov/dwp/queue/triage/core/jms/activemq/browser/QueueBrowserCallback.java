package uk.gov.dwp.queue.triage.core.jms.activemq.browser;

import org.springframework.jms.core.BrowserCallback;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.Enumeration;

public class QueueBrowserCallback implements BrowserCallback<Long> {

    private final FailedMessageListener failedMessageListener;

    public QueueBrowserCallback(FailedMessageListener failedMessageListener) {
        this.failedMessageListener = failedMessageListener;
    }

    @Override
    public Long doInJms(Session session, QueueBrowser browser) throws JMSException {
        long records = 0;
        final Enumeration enumeration = browser.getEnumeration();
        while (enumeration.hasMoreElements()) {
            final Message message = (Message) enumeration.nextElement();
            failedMessageListener.onMessage(message);
            records++;
        }
        return records;
    }
}
