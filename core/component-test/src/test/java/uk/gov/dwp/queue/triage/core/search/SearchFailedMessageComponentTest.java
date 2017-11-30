package uk.gov.dwp.queue.triage.core.search;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import java.util.Arrays;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage.noResults;

public class SearchFailedMessageComponentTest extends BaseCoreComponentTest<SearchFailedMessageStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;

    @Test
    public void searchByBrokerResultsNoResultsWhenNoFailedMessagesExist() throws Exception {

        when().aSearchIsRequested(newSearchFailedMessageRequest()
                .withBroker("broker-name"));

        then().theSearchResultsContain(noResults());
    }

    @Test
    public void searchByBrokerAndByQueue() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"))
                .exists();

        when().aSearchIsRequested(newSearchFailedMessageRequest()
                .withBroker("broker-name")
                .withDestination("another-queue")
        );

        then().theSearchResultsContain(noResults());

        when().aSearchIsRequested(newSearchFailedMessageRequest()
                .withBroker("broker-name")
                .withDestination("queue-name")
        );

        then().theSearchResultsContain(contains(
                aFailedMessage()
                        .withFailedMessageId(equalTo(failedMessageId))));
    }

    @Test
    public void searchByMessageContent() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"))
                .withContent("Bodger and Badger")
                .exists();

        when().aSearchIsRequested(newSearchFailedMessageRequest()
                .withContent("and")
        );

        then().theSearchResultsContain(contains(
                aFailedMessage()
                        .withFailedMessageId(equalTo(failedMessageId))));

        when().aSearchIsRequested(newSearchFailedMessageRequest()
                .withContent("Bananas")
        );

        then().theSearchResultsContain(noResults());
    }

    private static Matcher<Iterable<? extends SearchFailedMessageResponse>> contains(SearchFailedMessageResponseMatcher... searchResultMatcher) {
        return new IsIterableContainingInOrder<SearchFailedMessageResponse>(Arrays.asList(searchResultMatcher)) {
            @Override
            public String toString() {
                return new ReflectionArgumentFormatter().format(searchResultMatcher, "failedMessageId");
            }
        };
    }

}
