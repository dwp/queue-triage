package uk.gov.dwp.queue.triage.core.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public interface JmsMessageTransformer<M extends Message, T> {
    void transform(M jmsMessage, T data) throws JMSException;
}
