package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.jms.FailedMessageListenerComponentTest.contains;
import static uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage.noResults;

public class ClassifyFailedMessageComponentTest extends BaseCoreComponentTest<JmsStage> {

    @ScenarioStage
    private SearchFailedMessageStage searchFailedMessageStage;
    @ScenarioStage
    private MessageClassificationGivenStage messageClassificationGivenStage;
    @ScenarioStage
    private MessageClassificationWhenStage messageClassificationWhenStage;

    @Test
    public void messageIsClassifiedAndDeleted() throws Exception {
        messageClassificationGivenStage.given().aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue("poison", "any", "any");
        messageClassificationGivenStage.and().theMessageClassificationJobIsNotRunning();
        given().and().aMessageWithContent$WillDeadLetter("poison");

        when().aMessageWithContent$IsSentTo$OnBroker$("poison", "some-queue", "internal-broker");

        searchFailedMessageStage.aSearch$WillContain$(
                newSearchFailedMessageRequest().withBroker("internal-broker").withDestination("some-queue"),
                contains(aFailedMessage()
                        .withBroker(equalTo("internal-broker"))
                        .withDestination(equalTo(Optional.of("some-queue")))
                        .withContent(equalTo("poison"))
        ));

        messageClassificationWhenStage.when().theMessageClassificationJobExecutes();

        searchFailedMessageStage.then().aSearch$WillContain$(
                newSearchFailedMessageRequest().withBroker("internal-broker").withDestination("some-queue"),
                noResults()
        );
    }
}
