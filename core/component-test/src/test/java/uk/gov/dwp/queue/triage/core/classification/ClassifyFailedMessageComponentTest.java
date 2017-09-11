package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.stage.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.stage.JmsStage;
import uk.gov.dwp.queue.triage.core.stage.classification.MessageClassificationGivenStage;
import uk.gov.dwp.queue.triage.core.stage.classification.MessageClassificationWhenStage;
import uk.gov.dwp.queue.triage.core.stage.search.SearchFailedMessageStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.jms.FailedMessageListenerComponentTest.contains;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ClassifyFailedMessageComponentTest extends BaseCoreComponentTest<JmsStage> {

    private final FailedMessageId failedMessageId = newFailedMessageId();
    @ScenarioStage
    private SearchFailedMessageStage searchFailedMessageStage;
    @ScenarioStage
    private MessageClassificationGivenStage messageClassificationGivenStage;
    @ScenarioStage
    private MessageClassificationWhenStage messageClassificationWhenStage;
    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;

    @Before
    public void after() {
        messageClassificationGivenStage.noMessageClassifiersExist();
    }

    @Test
    public void messageClassifiedByContentIsDeleted() throws Exception {
        messageClassificationGivenStage.given().aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue("poison", "any", "any");
        messageClassificationGivenStage.and().theMessageClassificationJobIsNotRunning();
        failedMessageResourceStage.and().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(newFailedMessageId())
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("poison")
        ).exists();
        failedMessageResourceStage.and().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("nuts")
        ).exists();

        messageClassificationWhenStage.when().theMessageClassificationJobExecutes();

        searchFailedMessageStage.then().aSearch$WillContain$(
                newSearchFailedMessageRequest().withBroker("some-broker"),
                contains(aFailedMessage()
                        .withFailedMessageId(equalTo(failedMessageId))
                        .withContent(equalTo("nuts")))
        );
    }

    @Test
    public void messageClassifiedByContentAndBrokerIsDeleted() {
        messageClassificationGivenStage.given().aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue(
                "poison",
                "some-broker",
                "any");
        messageClassificationGivenStage.and().theMessageClassificationJobIsNotRunning();
        failedMessageResourceStage.and().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(newFailedMessageId())
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("poison")
        ).exists();
        failedMessageResourceStage.and().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("another-broker")
                .withDestinationName("some-queue")
                .withContent("poison")
        ).exists();

        messageClassificationWhenStage.when().theMessageClassificationJobExecutes();

        searchFailedMessageStage.then().aSearch$WillContain$(
                newSearchFailedMessageRequest().withDestination("some-queue"),
                contains(aFailedMessage().withFailedMessageId(equalTo(failedMessageId)))
        );
    }
}
