package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher.aFailedMessage;

@ActiveProfiles(value = "read-only-component-test", inheritProfiles = false)
public class ReadOnlyFailedMessageListenerComponentTest extends BaseCoreComponentTest<JmsStage> {

    @ScenarioStage
    private SearchFailedMessageStage searchFailedMessageStage;
    @ScenarioStage
    private FailedMessageListenerAdminGivenStage messageListenerGivenStage;
    @ScenarioStage
    private FailedMessageListenerAdminWhenStage messageListenerAdminWhenStage;

    @Test
    public void messageIsProcessedAndLeftOnTheQueueWhenRunningInReadOnlyMode() {
        given().aMessageWithContent$WillDeadLetter("poison");
        given().and().aMessageWithContent$WillBeConsumedSuccessfully("elixir");
        messageListenerGivenStage.given().and().theMesageListenerIsStoppedForBroker$("internal-broker");

        when().aMessageWithContent$IsSentTo$OnBroker$("poison", "some-queue", "internal-broker");
        when().and().aMessageWithContent$IsSentTo$OnBroker$("elixir", "some-queue", "internal-broker");
        then().deadLetterQueueContains$Messages(1);

        messageListenerAdminWhenStage.when().theMesageListenerReadsMessagesOnBroker$("internal-broker");

        searchFailedMessageStage.then().aSearch$WillContain$(
                searchMatchingAllCriteria().withBroker("internal-broker"),
                contains(aFailedMessage()
                        .withBroker(equalTo("internal-broker"))
                        .withDestination(equalTo(Optional.of("some-queue")))
                        .withContent(equalTo("poison"))
        ));
        then().and().deadLetterQueueStillContains$Messages(1);
    }

    @Test
    public void messageReadMultipleTimesIsOnlyPersistedOnce() {
        given().aMessageWithContent$WillDeadLetter("poison");
        given().and().aMessageWithContent$WillBeConsumedSuccessfully("elixir");
        messageListenerGivenStage.given().and().theMesageListenerIsStoppedForBroker$("internal-broker");

        when().aMessageWithContent$IsSentTo$OnBroker$("poison", "some-queue", "internal-broker");
        when().and().aMessageWithContent$IsSentTo$OnBroker$("elixir", "some-queue", "internal-broker");
        messageListenerAdminWhenStage.when().and().theMesageListenerReadsMessagesOnBroker$("internal-broker");
        messageListenerAdminWhenStage.when().and().theMesageListenerReadsMessagesOnBroker$("internal-broker");

        searchFailedMessageStage.then().aSearch$WillContain$(
                searchMatchingAllCriteria().withBroker("internal-broker"),
                contains(aFailedMessage()
                        .withBroker(equalTo("internal-broker"))
                        .withDestination(equalTo(Optional.of("some-queue")))
                        .withContent(equalTo("poison"))
                ));
        then().and().deadLetterQueueContains$Messages(1);
    }

    public static Matcher<Iterable<? extends SearchFailedMessageResponse>> contains(SearchFailedMessageResponseMatcher... searchResultMatcher) {
        return new IsIterableContainingInOrder<SearchFailedMessageResponse>(Arrays.asList(searchResultMatcher)) {
            @Override
            public String toString() {
                return "a message " + new ReflectionArgumentFormatter().format(searchResultMatcher, "broker", "destination");
            }
        };
    }
}
