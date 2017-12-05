package uk.gov.dwp.queue.triage.core.delete;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage.noResults;

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
        searchFailedMessageStage.and().aSearchIsRequested(searchMatchingAllCriteria()
                .withBroker("broker-name")
                .withDestination("queue-name")
        );

        searchFailedMessageStage.then().theSearchResultsContain(noResults());
    }
}
