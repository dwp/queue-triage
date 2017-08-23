package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage.noResults;

public class ClassifyFailedMessageComponentTest extends BaseCoreComponentTest<JmsStage> {

    @ScenarioStage
    private SearchFailedMessageStage searchFailedMessageStage;
    @ScenarioStage
    private MessageClassificationGivenStage messageClassificationGivenStage;

    @Test
    public void messageIsClassifiedAndDeleted() throws Exception {
        given().aMessageWithContent$WillDeadLetter("poison");
        messageClassificationGivenStage.and().aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue("poison", "any", "any");

        when().aMessageWithContent$IsSentTo$OnBroker$("poison", "some-queue", "internal-broker");

        searchFailedMessageStage.and().aSearchIsRequested(newSearchFailedMessageRequest()
                .withBroker("internal-broker")
                .withDestination("some-queue")
        );

        searchFailedMessageStage.then().theSearchResultsContain(noResults());
    }
}
