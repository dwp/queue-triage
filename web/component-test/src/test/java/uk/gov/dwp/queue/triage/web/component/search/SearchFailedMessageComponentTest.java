package uk.gov.dwp.queue.triage.web.component.search;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.component.WebComponentTest;
import uk.gov.dwp.queue.triage.web.component.list.FailedMessageListThenStage;
import uk.gov.dwp.queue.triage.web.component.list.ListFailedMessagesStage;
import uk.gov.dwp.queue.triage.web.component.login.LoginGivenStage;

import java.time.Instant;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequestMatcher.aSearchRequestMatchingAnyCriteria;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;

public class SearchFailedMessageComponentTest extends WebComponentTest<ListFailedMessagesStage, SearchFailedMessageWhenStage, FailedMessageListThenStage> {

    private static final FailedMessageId FAILED_MESSAGE_ID_1 = FailedMessageId.newFailedMessageId();

    @ScenarioStage
    private LoginGivenStage loginGivenStage;

    @Test
    public void searchAllFields() {
        loginGivenStage.given().theUserHasSuccessfullyLoggedOn();
        given().and().theUserNavigatesToTheFailedMessagesPage();
        given().and().queueTriageCoreWillRespondTo(
                aSearchRequestMatchingAnyCriteria()
                        .withBroker("some-text")
                        .withContent("some-text")
                        .withDestination("some-text"),
                newSearchFailedMessageResponse()
                        .withFailedMessageId(FAILED_MESSAGE_ID_1)
                        .withBroker("main-broker")
                        .withDestination("queue-name")
                        .withContent("This message contains some-text")
                        .withStatus(FailedMessageStatus.FAILED)
                        .withStatusDateTime(Instant.now())
                        .build()
        );

        when().theUserSearchesFor("some-text");

        then().theMessageListContainsAFailedMessageWithId(FAILED_MESSAGE_ID_1);
    }

    @Test
    public void searchForASpecificField() {
        loginGivenStage.given().theUserHasSuccessfullyLoggedOn();
        given().and().theUserNavigatesToTheFailedMessagesPage();
        given().and().queueTriageCoreWillRespondTo(
                aSearchRequestMatchingAnyCriteria().withBroker("main-broker"),
                newSearchFailedMessageResponse()
                        .withFailedMessageId(FAILED_MESSAGE_ID_1)
                        .withBroker("main-broker")
                        .withDestination("queue-name")
                        .withContent("This message contains some-text")
                        .withStatus(FailedMessageStatus.FAILED)
                        .withStatusDateTime(Instant.now())
                        .build()
        );

        when().theUserSearchesForTheField$EqualTo$("Broker", "main-broker");

        then().theMessageListContainsAFailedMessageWithId(FAILED_MESSAGE_ID_1);
    }
}
