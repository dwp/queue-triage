package uk.gov.dwp.queue.triage.core.search;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.CoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAnyCriteria;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage.noResults;

public class SearchFailedMessageComponentTest
        extends CoreComponentTestBase<FailedMessageResourceStage, SearchFailedMessageWhenStage, SearchFailedMessageThenStage> {

    @Test
    public void searchByBrokerResultsNoResultsWhenNoFailedMessagesExist() {

        when().aSearchIsRequestedForFailedMessages(searchMatchingAllCriteria()
                .withBroker("broker-name"));

        then().theSearchResultsContain(noResults());
    }

    @Test
    public void searchByBrokerAndByQueueWhenAllCriteriaNeedsBeToMatched() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"));

        when().aSearchIsRequestedForFailedMessages(searchMatchingAllCriteria()
                .withBroker("broker-name")
                .withDestination("another-queue")
        );

        then().theSearchResultsContain(noResults());

        when().aSearchIsRequestedForFailedMessages(searchMatchingAllCriteria()
                .withBroker("broker-name")
                .withDestination("queue-name")
        );

        then().theSearchResultsContain(contains(
                aFailedMessage()
                        .withFailedMessageId(equalTo(failedMessageId))));
    }

    @Test
    public void searchByBrokerAndByQueueWhenAnyCriteriaCanBeMatched() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        FailedMessageId anotherFailedMessageId = FailedMessageId.newFailedMessageId();
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name"));
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(anotherFailedMessageId)
                .withBrokerName("another-broker")
                .withDestinationName("queue-name"));

        when().aSearchIsRequestedForFailedMessages(searchMatchingAnyCriteria()
                .withBroker("some-broker")
                .withDestination("another-queue")
        );

        then().theSearchResultsContain(noResults());

        when().aSearchIsRequestedForFailedMessages(searchMatchingAnyCriteria()
                .withBroker("broker-name")
                .withDestination("queue-name")
        );

        then().theSearchResultsContain(containsInAnyOrder(
                aFailedMessage().withFailedMessageId(equalTo(failedMessageId)),
                aFailedMessage().withFailedMessageId(equalTo(anotherFailedMessageId))
        ));
    }

    @Test
    public void searchByMessageContentMatchesTextAnywhere() {
        FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("broker-name")
                .withDestinationName("queue-name")
                .withContent("Bodger and Badger"));

        when().aSearchIsRequestedForFailedMessages(searchMatchingAllCriteria().withContent("and"));

        then().theSearchResultsContain(contains(aFailedMessage().withFailedMessageId(equalTo(failedMessageId))));

        when().aSearchIsRequestedForFailedMessages(searchMatchingAllCriteria().withContent("Bananas"));

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
