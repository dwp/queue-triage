package uk.gov.dwp.queue.triage.core.status;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.SimpleCoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryResponseMatcher.statusHistoryResponse;

public class FailedMessageStatusHistoryComponentTest extends SimpleCoreComponentTestBase<StatusHistoryStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;

    @Test
    public void name() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"));
        when().theStatusHistoryIsRequestedFor(failedMessageId);
        then().theStatusHistory(Matchers.contains(
                statusHistoryResponse().withStatus(FailedMessageStatus.FAILED)
        ));
    }
}
