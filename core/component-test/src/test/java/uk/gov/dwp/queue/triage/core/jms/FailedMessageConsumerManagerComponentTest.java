package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.SimpleCoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage;

import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher.aFailedMessage;

public class FailedMessageConsumerManagerComponentTest extends SimpleCoreComponentTestBase<FailedMessageListenerAdminStage> {

    @ScenarioStage
    private JmsStage jmsStage;
    @ScenarioStage
    private FailedMessageListenerAdminWhenStage messageListenerAdminWhenStage;
    @ScenarioStage
    private SearchFailedMessageThenStage searchFailedMessageThenStage;

    @Test
    public void consumingFromQueueCanBeStoppedAndStarted() {
        jmsStage.given().aMessageWithContent$WillDeadLetter("poison");
        given().theMessageListenerFor$Is$Running("internal-broker", true);

        messageListenerAdminWhenStage.when().aRequestIsMadeToStopTheMessageListenerForBroker$("internal-broker");
        then().theMessageListenerFor$Is$Running("internal-broker", false);
        jmsStage.when().aMessageWithContent$IsSentTo$OnBroker$("poison", "some-queue", "internal-broker");

        searchFailedMessageThenStage.then().aSearch$ContainsNoResults(
                searchMatchingAllCriteria().withBroker("internal-broker")
        );
        messageListenerAdminWhenStage.when().aRequestIsMadeToStartTheMessageListenerForBroker$("internal-broker");

        then().theMessageListenerFor$Is$Running("internal-broker", true);
        searchFailedMessageThenStage.then().aSearch$WillContainAResponseWhere$(
                searchMatchingAllCriteria().withBroker("internal-broker"),
                aFailedMessage()
                        .withBroker(equalTo("internal-broker"))
                        .withDestination(equalTo(Optional.of("some-queue")))
                        .withContent(equalTo("poison"))
        );
    }

    @Test
    public void startingABrokerThatDoesNotExistReturnsNotFound() {
        messageListenerAdminWhenStage.when().aRequestIsMadeToStartTheMessageListenerForBroker$("unknown-broker");
        then().theMessageListenerAdminResourceRespondsWith$StatusCode(NOT_FOUND);
    }

    @Test
    public void stoppingABrokerThatDoesNotExistReturnsNotFound() {
        messageListenerAdminWhenStage.when().aRequestIsMadeToStopTheMessageListenerForBroker$("unknown-broker");
        then().theMessageListenerAdminResourceRespondsWith$StatusCode(NOT_FOUND);
    }

    @Test
    public void gettingTheStatusOfABrokerThatDoesNotExistReturnsNotFound() {
        messageListenerAdminWhenStage.when().aRequestIsMadeToGetTheStatusOfTheMessageListenerForBroker$("unknown-broker");
        then().theMessageListenerAdminResourceRespondsWith$StatusCode(NOT_FOUND);
    }
}
