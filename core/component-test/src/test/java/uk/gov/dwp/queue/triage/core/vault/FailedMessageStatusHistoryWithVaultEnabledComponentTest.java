package uk.gov.dwp.queue.triage.core.vault;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseVaultEnabledCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.status.StatusHistoryStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.hamcrest.Matchers.contains;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryResponseMatcher.statusHistoryResponse;

public class FailedMessageStatusHistoryWithVaultEnabledComponentTest extends BaseVaultEnabledCoreComponentTest<StatusHistoryStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;

    @Test
    public void ensureThatWhenRunningWithVaultConfigurationEnabled_thenFailedMessageStatusHistoryCanBeReadAndWrittenToAndFromTheDatabase() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"));
        when().theStatusHistoryIsRequestedFor(failedMessageId);
        then().theStatusHistory(contains(statusHistoryResponse().withStatus(FailedMessageStatus.FAILED)));
    }
}
