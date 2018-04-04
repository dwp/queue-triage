package uk.gov.dwp.queue.triage.core.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public interface JmsMessageDataProvider<M extends Message, T> {
    void provide(M jmsMessage, T data) throws JMSException;
}
