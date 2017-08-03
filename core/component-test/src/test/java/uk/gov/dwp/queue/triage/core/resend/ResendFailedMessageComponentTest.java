package uk.gov.dwp.queue.triage.core.resend;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.RESENDING;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ResendFailedMessageComponentTest extends BaseCoreComponentTest<JmsStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;
    @ScenarioStage
    private ResendFailedMessageWhenStage resendFailedMessageWhenStage;

    @Test
    public void name() throws Exception {
        FailedMessageId failedMessageId = newFailedMessageId();

        failedMessageResourceStage.given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal")
                .withDestinationName("some-queue")
        ).exists();

        resendFailedMessageWhenStage.when().aFailedMessageWithId$IsResent(failedMessageId);
        failedMessageResourceStage.and().aMessageWithId$IsSelected(failedMessageId);

        failedMessageResourceStage.thenTheFailedMessageReturned(aFailedMessage().withStatus(RESENDING));
    }
}
