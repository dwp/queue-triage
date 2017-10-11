package uk.gov.dwp.queue.triage.web.component.resend;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.component.WebComponentTest;
import uk.gov.dwp.queue.triage.web.component.list.ListFailedMessagesStage;
import uk.gov.dwp.queue.triage.web.component.login.LoginGivenStage;

import java.time.Instant;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;

public class ResendFailedMessageComponentTest extends WebComponentTest<ListFailedMessagesStage, ResendMessageWhenStage, ResendMessageThenStage> {

    private static final FailedMessageId FAILED_MESSAGE_ID_1 = FailedMessageId.newFailedMessageId();
    private static final FailedMessageId FAILED_MESSAGE_ID_2 = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();

    @ScenarioStage
    private LoginGivenStage loginGivenStage;

    @Test
    public void resendFailedMessageTest() throws Exception {
        given().aFailedMessage$Exists(newSearchFailedMessageResponse()
                .withFailedMessageId(FAILED_MESSAGE_ID_1)
                .withBroker("main-broker")
                .withDestination(Optional.of("queue-name"))
                .withSentDateTime(NOW.minus(1, MINUTES))
                .withFailedDateTime(NOW)
                .withContent("Boom")
        );
        given().and().aFailedMessage$Exists(newSearchFailedMessageResponse()
                .withFailedMessageId(FAILED_MESSAGE_ID_2)
                .withBroker("another-broker")
                .withDestination(Optional.of("topic-name"))
                .withSentDateTime(NOW.minus(1, HOURS))
                .withFailedDateTime(NOW.minus(2, MINUTES))
                .withContent("Failure")
        );
        given().and().theSearchResultsWillContainFailedMessages$(FAILED_MESSAGE_ID_1, FAILED_MESSAGE_ID_2);
        loginGivenStage.given().and().theUserHasSuccessfullyLoggedOn();
        given().and().theUserHasNavigatedToTheFailedMessagesPage();

        when().failedMessage$IsSelected(FAILED_MESSAGE_ID_1);
        when().and().theUserClicksResend();
//        when().and().theUserConfirmsTheyWantToResendTheMessages();

        then().failedMessage$IsResent(FAILED_MESSAGE_ID_1);
        then().failedMessage$IsNotResent(FAILED_MESSAGE_ID_2);
    }
}
