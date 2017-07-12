package uk.gov.dwp.queue.triage.core.dao;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

public interface FailedMessageDao {

    void insert(FailedMessage failedMessage);

    FailedMessage findById(FailedMessageId failedMessageId);

    long findNumberOfMessagesForBroker(String broker);
}
