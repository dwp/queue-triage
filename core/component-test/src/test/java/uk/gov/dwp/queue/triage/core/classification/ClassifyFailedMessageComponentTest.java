package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
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
    public void setUp() {
        messageClassificationGivenStage.given().noMessageClassifiersExist();
        messageClassificationGivenStage.given().and().theMessageClassificationJobIsNotRunning();
    }

    @Test
    public void messageClassifiedByContentIsDeleted() throws Exception {
        messageClassificationGivenStage.given().and().aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue("poison", "any", "any");
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
                searchMatchingAllCriteria().withBroker("some-broker"),
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
                searchMatchingAllCriteria().withDestination("some-queue"),
                contains(aFailedMessage().withFailedMessageId(equalTo(failedMessageId)))
        );
    }

    @Test
    public void addALabelToMessageAfterClassification() {
        messageClassificationGivenStage.given().aMessageClassifierExistsToLabelAnyMessage$FromBroker$(
                "investigate",
                "some-broker");
        failedMessageResourceStage.and().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("poison")
        ).exists();

        messageClassificationWhenStage.when().theMessageClassificationJobExecutes();

        failedMessageResourceStage.then().aFailedMessageWithId$Has(
                failedMessageId, FailedMessageResponseMatcher.aFailedMessage().withLabels(Matchers.contains("investigate")));
    }
}
