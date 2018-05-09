package uk.gov.dwp.queue.triage.core.resend;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.CoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.delete.DeleteFailedMessageStage;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Duration;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.RESENDING;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.SENT;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ResendFailedMessageComponentTest
        extends CoreComponentTestBase<ResendFailedMessageGivenStage, ResendFailedMessageWhenStage, SearchFailedMessageThenStage> {

    @ScenarioStage
    private DeleteFailedMessageStage deleteFailedMessageStage;
    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;
    @ScenarioStage
    private SearchFailedMessageThenStage searchFailedMessageThenStage;
    @ScenarioStage
    private ResendFailedMessageGivenStage resendFailedMessageGivenStage;
    @ScenarioStage
    private ResendFailedMessageWhenStage resendFailedMessageWhenStage;
    @ScenarioStage
    private JmsStage jmsStage;

    @Test
    public void markMessageForResending() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        FailedMessageId failedMessageIdToResend = newFailedMessageId();

        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal-broker")
                .withDestinationName("some-queue"));
        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageIdToResend)
                .withBrokerName("internal-broker")
                .withDestinationName("some-queue")
                .withContent("elixir"));
        given().theResendFailedMessageJobIsPausedForBroker$("internal-broker");

        jmsStage.aMessageWithContent$WillBeConsumedSuccessfully("elixir");
        when().aFailedMessageWithId$IsMarkedForResend(failedMessageIdToResend);

        then().aFailedMessageWithId$Has(failedMessageIdToResend, aFailedMessage().withStatus(RESENDING));
        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withStatus(FAILED));

        when().theResendFailedMessageJobExecutesForBroker$("internal-broker");
        then().aFailedMessageWithId$Has(failedMessageIdToResend, aFailedMessage().withStatus(SENT));
    }

    @Test
    public void messagesMarkedForResendingInTheFutureAreNotSent() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();

        given().theResendFailedMessageJobIsPausedForBroker$("internal-broker");
        failedMessageResourceStage.given().and().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal-broker")
                .withDestinationName("some-queue"));

        when().aFailedMessageWithId$IsMarkedForResendIn$(failedMessageId, Duration.ofMinutes(30));

        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withStatus(RESENDING));

        when().theResendFailedMessageJobExecutesForBroker$("internal-broker");
        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withStatus(RESENDING));
    }

    @Test
    public void messageMarkedForResendingInTheFutureCanBeDeleted() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();

        given().theResendFailedMessageJobIsPausedForBroker$("internal-broker");
        failedMessageResourceStage.given().and().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal-broker")
                .withDestinationName("some-queue"));


        when().aFailedMessageWithId$IsMarkedForResendIn$(failedMessageId, Duration.ofMinutes(30));
        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withStatus(RESENDING));

        deleteFailedMessageStage.when().theFailedMessageWithId$IsDeleted(failedMessageId);
        then().aFailedMessageWithId$DoesNotExist(failedMessageId);
    }
}
