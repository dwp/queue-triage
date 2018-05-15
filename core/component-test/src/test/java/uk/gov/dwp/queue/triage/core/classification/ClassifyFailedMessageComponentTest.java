package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.JmsStage;
import uk.gov.dwp.queue.triage.core.SimpleCoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ClassifyFailedMessageComponentTest extends SimpleCoreComponentTestBase<JmsStage> {

    private final FailedMessageId failedMessageId = newFailedMessageId();
    @ScenarioStage
    private SearchFailedMessageThenStage searchFailedMessageThenStage;
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
    public void messageClassifiedByContentIsDeleted() {
        messageClassificationGivenStage.given().and().aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue("poison", "any", "any");
        failedMessageResourceStage.and().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(newFailedMessageId())
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("poison"));
        failedMessageResourceStage.and().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("nuts"));

        messageClassificationWhenStage.when().theMessageClassificationJobExecutes();

        searchFailedMessageThenStage.then().aSearch$WillContainAResponseWhere$(
                searchMatchingAllCriteria().withBroker("some-broker"),
                aFailedMessage()
                        .withFailedMessageId(equalTo(failedMessageId))
                        .withContent(equalTo("nuts"))
        );
    }

    @Test
    public void messageClassifiedByContentAndBrokerIsDeleted() {
        messageClassificationGivenStage.given().aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue(
                "poison",
                "some-broker",
                "any");
        failedMessageResourceStage.and().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(newFailedMessageId())
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("poison"));
        failedMessageResourceStage.and().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("another-broker")
                .withDestinationName("some-queue")
                .withContent("poison"));

        messageClassificationWhenStage.when().theMessageClassificationJobExecutes();

        searchFailedMessageThenStage.then().aSearch$WillContainAResponseWhere$(
                searchMatchingAllCriteria().withDestination("some-queue"),
                aFailedMessage().withFailedMessageId(equalTo(failedMessageId))
        );
    }

    @Test
    public void addALabelToMessageAfterClassification() {
        messageClassificationGivenStage.given().aMessageClassifierExistsToLabelAnyMessage$FromBroker$(
                "investigate",
                "some-broker");
        failedMessageResourceStage.and().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withContent("poison"));

        messageClassificationWhenStage.when().theMessageClassificationJobExecutes();

        searchFailedMessageThenStage.then().aFailedMessageWithId$Has(
                failedMessageId, FailedMessageResponseMatcher.aFailedMessage().withLabels(Matchers.contains("investigate")));
    }
}
