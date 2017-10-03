package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage;

import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher.aFailedMessage;

public class FailedMessageListenerAdminComponentTest extends BaseCoreComponentTest<FailedMessageListenerAdminStage> {

    @ScenarioStage
    private JmsStage jmsStage;
    @ScenarioStage
    private SearchFailedMessageStage searchFailedMessageStage;

    @Test
    public void consumingFromQueueCanBeStoppedAndStarted() throws Exception {
        jmsStage.given().aMessageWithContent$WillDeadLetter("poison");
        given().theMessageListenerFor$Is$Running("internal-broker", true);

        when().aRequestIsMadeToStopTheMessageListenerForBroker$("internal-broker");
        then().theMessageListenerFor$Is$Running("internal-broker", false);
        jmsStage.when().aMessageWithContent$IsSentTo$OnBroker$("poison", "some-queue", "internal-broker");

        searchFailedMessageStage.then().aSearch$WillContain$(
                newSearchFailedMessageRequest().withBroker("internal-broker"),
                Matchers.emptyIterable()
        );
        when().aRequestIsMadeToStartTheMessageListenerForBroker$("internal-broker");

        then().theMessageListenerFor$Is$Running("internal-broker", true);
        searchFailedMessageStage.then().aSearch$WillContain$(
                newSearchFailedMessageRequest().withBroker("internal-broker"),
                contains(aFailedMessage()
                        .withBroker(equalTo("internal-broker"))
                        .withDestination(equalTo(Optional.of("some-queue")))
                        .withContent(equalTo("poison")))
        );
    }

    @Test
    public void startingABrokerThatDoesNotExistReturnsNotFound() {
        when().aRequestIsMadeToStartTheMessageListenerForBroker$("unknown-broker");
        then().theMessageListenerAdminResourceRespondsWith$StatusCode(NOT_FOUND);
    }

    @Test
    public void stoppingABrokerThatDoesNotExistReturnsNotFound() {
        when().aRequestIsMadeToStopTheMessageListenerForBroker$("unknown-broker");
        then().theMessageListenerAdminResourceRespondsWith$StatusCode(NOT_FOUND);
    }

    @Test
    public void gettingTheStatusOfABrokerThatDoesNotExistReturnsNotFound() {
        when().aRequestIsMadeToGetTheStatusOfTheMessageListenerForBroker$("unknown-broker");
        then().theMessageListenerAdminResourceRespondsWith$StatusCode(NOT_FOUND);
    }
}
