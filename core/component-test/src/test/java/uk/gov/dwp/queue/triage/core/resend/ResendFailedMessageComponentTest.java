package uk.gov.dwp.queue.triage.core.resend;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.stage.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.stage.JmsStage;
import uk.gov.dwp.queue.triage.core.stage.resend.resend.ResendFailedMessageGivenStage;
import uk.gov.dwp.queue.triage.core.stage.resend.resend.ResendFailedMessageWhenStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.RESENDING;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.SENT;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ResendFailedMessageComponentTest extends BaseCoreComponentTest<JmsStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;
    @ScenarioStage
    private ResendFailedMessageGivenStage resendFailedMessageGivenStage;
    @ScenarioStage
    private ResendFailedMessageWhenStage resendFailedMessageWhenStage;
    @ScenarioStage
    private JmsStage jmsStage;

    @Test
    public void markMessageForResending() throws Exception {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        FailedMessageId failedMessageIdToResend = newFailedMessageId();

        failedMessageResourceStage.given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal-broker")
                .withDestinationName("some-queue")
        ).exists();
        failedMessageResourceStage.given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageIdToResend)
                .withBrokerName("internal-broker")
                .withDestinationName("some-queue")
                .withContent("elixir")
        ).exists();
        resendFailedMessageGivenStage.given().theResendFailedMessageJobIsPausedForBroker$("internal-broker");

        jmsStage.aMessageWithContent$WillBeConsumedSuccessfully("elixir");
        resendFailedMessageWhenStage.when().aFailedMessageWithId$IsMarkedForResend(failedMessageIdToResend);

        failedMessageResourceStage.then().aFailedMessageWithId$Has(failedMessageIdToResend, aFailedMessage().withStatus(RESENDING));
        failedMessageResourceStage.then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withStatus(FAILED));

        resendFailedMessageWhenStage.when().theResendFailedMessageJobExecutesForBroker$("internal-broker");
        failedMessageResourceStage.then().aFailedMessageWithId$Has(failedMessageIdToResend, aFailedMessage().withStatus(SENT));
    }
}
