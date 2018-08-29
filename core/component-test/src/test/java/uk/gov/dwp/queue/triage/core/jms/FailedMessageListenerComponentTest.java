package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.SimpleCoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.FAILED_MESSAGE_ID;

public class FailedMessageListenerComponentTest extends SimpleCoreComponentTestBase<JmsStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;
    @ScenarioStage
    private SearchFailedMessageThenStage searchFailedMessageThenStage;

    @Test
    public void deadLetteredMessageIsProcessedByTheApplication() {
        given().aMessageWithContent$WillDeadLetter("poison");
        given().and().aMessageWithContent$WillBeConsumedSuccessfully("elixir");

        when().aMessageWithContent$IsSentTo$OnBroker$("poison", "some-queue", "internal-broker");
        when().and().aMessageWithContent$IsSentTo$OnBroker$("elixir", "some-queue", "internal-broker");

        searchFailedMessageThenStage.then().aSearch$WillContainAResponseWhere$(
                searchMatchingAllCriteria().withBroker("internal-broker"),
                aFailedMessage()
                        .withBroker(equalTo("internal-broker"))
                        .withDestination(equalTo(Optional.of("some-queue")))
                        .withContent(equalTo("poison"))
                        .withJmsMessageId(Matchers.notNullValue(String.class))
        );
    }

    @Test
    public void existingFailedMessageIsUpdatedIfItDeadLettersAgain() {
        final FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal-broker")
                .withDestinationName("some-queue")
                .withContent("some content")
                .withLabel("foo"));
        given().and().aMessageWithContent$WillDeadLetter("poison");

        when().aMessage$IsSentTo$OnBroker$(new TextMessageBuilder()
                .withContent("poison")
                .withProperty(FAILED_MESSAGE_ID, failedMessageId.toString()),
                "some-queue",
                "internal-broker"
        );

        searchFailedMessageThenStage.then().aSearch$WillContainAResponseWhere$(
                searchMatchingAllCriteria().withBroker("internal-broker"),
                aFailedMessage()
                        .withBroker(equalTo("internal-broker"))
                        .withDestination(equalTo(Optional.of("some-queue")))
                        .withContent(equalTo("poison"))
                        .withJmsMessageId(Matchers.notNullValue(String.class))
                        .withLabels(Matchers.contains("foo"))
        );
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
