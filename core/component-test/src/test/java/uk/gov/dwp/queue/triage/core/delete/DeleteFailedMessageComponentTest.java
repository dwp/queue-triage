package uk.gov.dwp.queue.triage.core.delete;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.SimpleCoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageWhenStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage.noResults;

public class DeleteFailedMessageComponentTest extends SimpleCoreComponentTestBase<DeleteFailedMessageStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;
    @ScenarioStage
    private SearchFailedMessageWhenStage searchFailedMessageWhenStage;
    @ScenarioStage
    private SearchFailedMessageThenStage searchFailedMessageThenStage;

    @Test
    public void deleteFailedMessage() throws Exception {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"));

        when().theFailedMessageWithId$IsDeleted(failedMessageId);
        searchFailedMessageWhenStage.and().aSearchIsRequestedForFailedMessages(searchMatchingAllCriteria()
                .withBroker("broker-name")
                .withDestination("queue-name")
        );

        searchFailedMessageThenStage.then().theSearchResultsContain(noResults());
    }
}
