package uk.gov.dwp.queue.triage.web.component.status;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.client.status.FailedMessageStatusHistoryClient;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.GivenStage;

import java.util.Arrays;

import static org.mockito.Mockito.when;

@JGivenStage
public class StatusHistoryGivenStage extends GivenStage<StatusHistoryGivenStage> {

    @Autowired
    private FailedMessageStatusHistoryClient failedMessageStatusHistoryClient;

    // Given a Failed Message With Id {} with Status {} on {} and with Status {} on {}
    public StatusHistoryGivenStage aFailedMessageWithId$Has$(
            FailedMessageId failedMessageId,
            @Table StatusHistoryResponse...statusHistoryResponses) {
        when(failedMessageStatusHistoryClient.getStatusHistory(failedMessageId)).thenReturn(Arrays.asList(statusHistoryResponses));
        return self();
    }
}
