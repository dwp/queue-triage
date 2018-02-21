package uk.gov.dwp.queue.triage.core.jms;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import javax.jms.JMSException;
import javax.jms.Message;

public interface FailedMessageFactory {

    FailedMessage createFailedMessage(Message message) throws JMSException;
}
