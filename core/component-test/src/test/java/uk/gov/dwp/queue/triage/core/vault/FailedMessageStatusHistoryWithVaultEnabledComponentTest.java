package uk.gov.dwp.queue.triage.core.vault;

import com.tngtech.jgiven.annotation.ScenarioStage;

import org.hamcrest.Matchers;
import org.junit.Test;

import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.BaseVaultEnabledCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.status.StatusHistoryStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryResponseMatcher.statusHistoryResponse;

public class FailedMessageStatusHistoryWithVaultEnabledComponentTest extends BaseVaultEnabledCoreComponentTest<StatusHistoryStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;

    @Test
    public void ensureThatWhenRunningWithVaultConfigurationEnabled_thenFailedMessageStatusHistoryCanBeReadAndWrittenToAndFromTheDatabase() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"))
                .exists();
        when().theStatusHistoryIsRequestedFor(failedMessageId);
        then().theStatusHistory(Matchers.contains(
                statusHistoryResponse().withStatus(FailedMessageStatus.FAILED)
        ));
    }
}
