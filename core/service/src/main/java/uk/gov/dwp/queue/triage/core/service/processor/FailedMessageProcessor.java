package uk.gov.dwp.queue.triage.core.service.processor;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public interface FailedMessageProcessor {

    void process(FailedMessage failedMessage);
}
