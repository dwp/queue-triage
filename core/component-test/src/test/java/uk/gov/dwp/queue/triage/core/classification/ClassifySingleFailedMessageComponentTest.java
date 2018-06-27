package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.CoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.classification.action.DeleteMessageAction;
import uk.gov.dwp.queue.triage.core.classification.action.LabelMessageAction;
import uk.gov.dwp.queue.triage.core.classification.classifier.DelegatingMessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.classifier.ExecutingMessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup;
import uk.gov.dwp.queue.triage.core.classification.predicate.BrokerEqualsPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.ContentContainsJsonPath;
import uk.gov.dwp.queue.triage.core.classification.predicate.DestinationEqualsPredicate;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Optional;

import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ClassifySingleFailedMessageComponentTest
        extends CoreComponentTestBase<MessageClassificationGivenStage, MessageClassificationWhenStage, MessageClassificationThenStage> {

    private final FailedMessageId failedMessageId = newFailedMessageId();

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;

    @Test
    public void setUp() {
        given().noMessageClassifiersExist();
    }

    @Test
    public void allClassifiersAreExecutedNoneMatch() {
        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal-broker")
                .withDestinationName("another-queue")
                .withContent("poison"));

        given().and().theFollowingMessageClassifiersExist(
                new DelegatingMessageClassifier(
                        new BrokerEqualsPredicate("internal-broker"),
                        MessageClassifierGroup.newClassifierCollection()
                                .withClassifier(new ExecutingMessageClassifier(
                                        new DestinationEqualsPredicate(Optional.ofNullable("some-queue")),
                                        new LabelMessageAction("foo", null)
                                ))
                                .withClassifier(new DelegatingMessageClassifier(
                                        new DestinationEqualsPredicate(Optional.ofNullable("another-queue")),
                                        new ExecutingMessageClassifier(
                                                new ContentContainsJsonPath("$.foo"),
                                                new DeleteMessageAction(null)))
                                )
                                .build()
                )
        );

        when().failedMessage$IsClassified(failedMessageId);
        then().theFailedMessageWas$Matched(false)
                .and()
                .producedDescription$("( broker = 'internal-broker' [true] AND ( ( destination = 'some-queue' [false] ) OR ( destination = 'another-queue' [true] AND content contains json path $.foo [false] ) ) )")
                .and()
                .failedMessageActionWas$("null");
    }

    @Test
    public void allClassifiersAreExecutedLastMatches() {
        failedMessageResourceStage.given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withBrokerName("internal-broker")
                .withDestinationName("another-queue")
                .withContent("{ \"foo\": \"bar\" }"));

        given().and().theFollowingMessageClassifiersExist(
                new DelegatingMessageClassifier(
                        new BrokerEqualsPredicate("internal-broker"),
                        MessageClassifierGroup.newClassifierCollection()
                                .withClassifier(new ExecutingMessageClassifier(
                                        new DestinationEqualsPredicate(Optional.ofNullable("some-queue")),
                                        new LabelMessageAction("foo", null)
                                ))
                                .withClassifier(new DelegatingMessageClassifier(
                                        new DestinationEqualsPredicate(Optional.ofNullable("another-queue")),
                                        new ExecutingMessageClassifier(
                                                new ContentContainsJsonPath("$.foo"),
                                                new DeleteMessageAction(null)))
                                )
                                .build()
                )
        );

        when().failedMessage$IsClassified(failedMessageId);
        then().theFailedMessageWas$Matched(true)
                .and()
                .producedDescription$("( broker = 'internal-broker' [true] AND ( ( destination = 'some-queue' [false] ) OR ( destination = 'another-queue' [true] AND content contains json path $.foo [true] ) ) )")
                .and()
                .failedMessageActionWas$("{\"_action\":\"delete\"}");

    }

}
