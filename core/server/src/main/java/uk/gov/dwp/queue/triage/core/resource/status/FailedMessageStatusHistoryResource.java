package uk.gov.dwp.queue.triage.core.resource.status;

import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.status.FailedMessageStatusHistoryClient;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter.toFailedMessageStatus;

public class FailedMessageStatusHistoryResource implements FailedMessageStatusHistoryClient {

    private final FailedMessageDao failedMessageDao;

    public FailedMessageStatusHistoryResource(FailedMessageDao failedMessageDao) {
        this.failedMessageDao = failedMessageDao;
    }

    @Override
    public List<StatusHistoryResponse> getStatusHistory(FailedMessageId failedMessageId) {
        final List<StatusHistoryResponse> statusHistoryResponses = new ArrayList<>();
        final List<StatusHistoryEvent> statusHistory = failedMessageDao.getStatusHistory(failedMessageId);
        for (int i=0; i<statusHistory.size(); i++) {
            final FailedMessageStatus failedMessageStatus = toFailedMessageStatus(statusHistory.get(i).getStatus());
            if (i+1<statusHistory.size() && failedMessageStatus.equals(toFailedMessageStatus(statusHistory.get(i+1).getStatus()))) {
                i++;
            }
            statusHistoryResponses.add(new StatusHistoryResponse(failedMessageStatus, statusHistory.get(i).getEffectiveDateTime()));
        }
        return statusHistoryResponses;
    }
}
