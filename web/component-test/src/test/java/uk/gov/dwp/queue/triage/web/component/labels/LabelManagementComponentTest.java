package uk.gov.dwp.queue.triage.web.component.labels;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.component.BaseWebComponentTest;
import uk.gov.dwp.queue.triage.web.component.list.ListFailedMessagesStage;
import uk.gov.dwp.queue.triage.web.component.login.LoginGivenStage;

import java.time.Instant;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;

public class LabelManagementComponentTest extends BaseWebComponentTest<LabelManagementWhenStage> {

    private static final FailedMessageId FAILED_MESSAGE_ID_1 = FailedMessageId.newFailedMessageId();
    private static final FailedMessageId FAILED_MESSAGE_ID_2 = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();

    @ScenarioStage
    private LoginGivenStage loginGivenStage;
    @ScenarioStage
    private ListFailedMessagesStage listFailedMessagesStage;
    @ScenarioStage
    private LabelManagementThenStage labelManagementThenStage;

    @Test
    public void addSingleLabelToAFailedMessage() throws Exception {
        listFailedMessagesStage.given().aFailedMessage$Exists(newSearchFailedMessageResponse()
                .withFailedMessageId(FAILED_MESSAGE_ID_1)
                .withBroker("main-broker")
                .withDestination(Optional.of("queue-name"))
                .withSentDateTime(NOW.minus(1, MINUTES))
                .withFailedDateTime(NOW)
                .withContent("Boom")
        );
        listFailedMessagesStage.given().and().aFailedMessage$Exists(newSearchFailedMessageResponse()
                .withFailedMessageId(FAILED_MESSAGE_ID_2)
                .withBroker("another-broker")
                .withDestination(Optional.of("topic-name"))
                .withSentDateTime(NOW.minus(1, HOURS))
                .withFailedDateTime(NOW.minus(2, MINUTES))
                .withContent("Failure")
        );
        listFailedMessagesStage.given().and().theSearchResultsWillContainFailedMessages$(FAILED_MESSAGE_ID_1, FAILED_MESSAGE_ID_2);
        loginGivenStage.given().and().theUserHasSuccessfullyLoggedOn();
        listFailedMessagesStage.given().and().theUserHasNavigatedToTheFailedMessagesPage();

        when().theUserAddslabels$ToFailedMessage$("foo", FAILED_MESSAGE_ID_1);
        when().and().theUserAddslabels$ToFailedMessage$("label1, label2", FAILED_MESSAGE_ID_2);
        when().and().theUserClicksSave();

        labelManagementThenStage.then().failedMessage$IsUpdatedWithLabels$(FAILED_MESSAGE_ID_1, "foo");
        labelManagementThenStage.then().and().failedMessage$IsUpdatedWithLabels$(FAILED_MESSAGE_ID_2, "label1", "label2");
    }

    @Test
    public void removeLabelsFromAFailedMessage() throws Exception {
        listFailedMessagesStage.given().aFailedMessage$Exists(newSearchFailedMessageResponse()
                .withFailedMessageId(FAILED_MESSAGE_ID_1)
                .withBroker("main-broker")
                .withDestination(Optional.of("queue-name"))
                .withSentDateTime(NOW.minus(1, MINUTES))
                .withFailedDateTime(NOW)
                .withContent("Boom")
        );
        listFailedMessagesStage.given().and().aFailedMessage$Exists(newSearchFailedMessageResponse()
                .withFailedMessageId(FAILED_MESSAGE_ID_2)
                .withBroker("another-broker")
                .withDestination(Optional.of("topic-name"))
                .withSentDateTime(NOW.minus(1, HOURS))
                .withFailedDateTime(NOW.minus(2, MINUTES))
                .withContent("Failure")
        );
        listFailedMessagesStage.given().and().theSearchResultsWillContainFailedMessages$(FAILED_MESSAGE_ID_1, FAILED_MESSAGE_ID_2);
        loginGivenStage.given().and().theUserHasSuccessfullyLoggedOn();
        listFailedMessagesStage.given().and().theUserHasNavigatedToTheFailedMessagesPage();

        when().theUserAddslabels$ToFailedMessage$("", FAILED_MESSAGE_ID_1);
        when().and().theUserClicksSave();

        labelManagementThenStage.then().failedMessage$IsUpdatedWithNoLabels(FAILED_MESSAGE_ID_1);
        labelManagementThenStage.then().and().failedMessage$IsNotUpdated(FAILED_MESSAGE_ID_2);
    }
}
