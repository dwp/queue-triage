package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import java.util.Arrays;
import java.util.Optional;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher.aFailedMessage;

public class FailedMessageListenerComponentTest extends BaseCoreComponentTest<JmsStage> {

    @ScenarioStage
    private SearchFailedMessageStage searchFailedMessageStage;

    @Test
    public void deadLetteredMessageIsProcessedByTheApplication() throws Exception {
        given().anApplicationIsListeningTo$OnBroker$("some-queue", "internal-broker");

        when().anInvalidMessageIsSentTo$OnBroker$("some-queue", "internal-broker");
        searchFailedMessageStage.and().aSearchIsRequested(newSearchFailedMessageRequest().withBroker("internal-broker"));

        searchFailedMessageStage.then().theSearchResultsContain(contains(
                aFailedMessage().withBroker(Matchers.equalTo("internal-broker"))
                .withDestination(Matchers.equalTo(Optional.of("some-queue")))
        ));
    }

    public static Matcher<Iterable<? extends SearchFailedMessageResponse>> contains(SearchFailedMessageResponseMatcher... searchResultMatcher) {
        return new IsIterableContainingInOrder<SearchFailedMessageResponse>(Arrays.asList(searchResultMatcher)) {
            @Override
            public String toString() {
                return new ReflectionArgumentFormatter().format(searchResultMatcher, "broker", "destination");
            }
        };
    }

}
