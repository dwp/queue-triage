package uk.gov.dwp.queue.triage.core.jms.activemq.browser;

import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;

public class QueueBrowserService<T> {

    private final BrowserCallback<T> browserCallback;
    private final JmsTemplate jmsTemplate;
    private final String brokerName;
    private final String queueName;

    public QueueBrowserService(BrowserCallback<T> browserCallback,
                               JmsTemplate jmsTemplate,
                               String brokerName,
                               String queueName) {
        this.browserCallback = browserCallback;
        this.jmsTemplate = jmsTemplate;
        this.brokerName = brokerName;
        this.queueName = queueName;
    }

    public void browse() {
        jmsTemplate.browse(queueName, browserCallback);
    }

    public String getBrokerName() {
        return brokerName;
    }
}
