package uk.gov.dwp.queue.triage.core.dao;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.List;

public interface FailedMessageDao {

    void insert(FailedMessage failedMessage);

    FailedMessage findById(FailedMessageId failedMessageId);

    long findNumberOfMessagesForBroker(String broker);

    void updateStatus(FailedMessageId failedMessageId, FailedMessageStatus failedMessageStatus);

    List<FailedMessageStatus> getStatusHistory(FailedMessageId failedMessageId);
}
