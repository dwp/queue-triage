package uk.gov.dwp.queue.triage.core.jms.activemq.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.BrowserCallback;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.Enumeration;

public class QueueBrowserCallback implements BrowserCallback<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueBrowserCallback.class);

    private final FailedMessageListener failedMessageListener;

    public QueueBrowserCallback(FailedMessageListener failedMessageListener) {
        this.failedMessageListener = failedMessageListener;
    }

    @Override
    public Long doInJms(Session session, QueueBrowser browser) throws JMSException {
        long records = 0;
        LOGGER.debug("Browsing messages on {}", browser.getQueue());
        final Enumeration enumeration = browser.getEnumeration();
        while (enumeration.hasMoreElements()) {
            final Message message = (Message) enumeration.nextElement();
            failedMessageListener.onMessage(message);
            records++;
        }
        LOGGER.debug("Processed {} messages on {}", records, browser.getQueue());
        return records;
    }
}
