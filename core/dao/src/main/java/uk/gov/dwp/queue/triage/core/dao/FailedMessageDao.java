package uk.gov.dwp.queue.triage.core.dao;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.List;
import java.util.Set;

public interface FailedMessageDao {

    void insert(FailedMessage failedMessage);

    FailedMessage findById(FailedMessageId failedMessageId);

    long findNumberOfMessagesForBroker(String broker);

    void update(FailedMessage failedMessage);

    void updateStatus(FailedMessageId failedMessageId, StatusHistoryEvent statusHistoryEvent);

    List<StatusHistoryEvent> getStatusHistory(FailedMessageId failedMessageId);

    int removeFailedMessages();

    void addLabel(FailedMessageId failedMessageId, String label);

    void setLabels(FailedMessageId failedMessageId, Set<String> labels);

    void removeLabel(FailedMessageId failedMessageId, String label);
}
