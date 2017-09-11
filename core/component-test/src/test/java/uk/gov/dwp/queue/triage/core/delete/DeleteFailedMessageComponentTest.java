package uk.gov.dwp.queue.triage.core.delete;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.stage.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.stage.delete.DeleteFailedMessageStage;
import uk.gov.dwp.queue.triage.core.stage.search.SearchFailedMessageStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.stage.search.SearchFailedMessageStage.noResults;

public class DeleteFailedMessageComponentTest extends BaseCoreComponentTest<DeleteFailedMessageStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;
    @ScenarioStage
    private SearchFailedMessageStage searchFailedMessageStage;

    @Test
    public void deleteFailedMessage() throws Exception {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"))
                .exists();

        when().theFailedMessageWithId$IsDeleted(failedMessageId);
        searchFailedMessageStage.and().aSearchIsRequested(newSearchFailedMessageRequest()
                .withBroker("broker-name")
                .withDestination("queue-name")
        );

        searchFailedMessageStage.then().theSearchResultsContain(noResults());
    }
}
