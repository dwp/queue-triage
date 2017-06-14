package uk.gov.dwp.queue.triage.core.dao;

import uk.gov.dwp.queue.triage.core.client.FailedMessage;
import uk.gov.dwp.queue.triage.core.client.FailedMessageId;

public interface FailedMessageDao {

    void insert(FailedMessage failedMessage);

    FailedMessage findById(FailedMessageId failedMessageId);

}
