package uk.gov.dwp.queue.triage.core.jms;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public interface MessageSender {

    void send(FailedMessage failedMessage);
}
