package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import uk.gov.dwp.queue.triage.core.classification.action.DeleteMessageAction;
import uk.gov.dwp.queue.triage.core.classification.action.LabelMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.AndPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.BrokerEqualsPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.ContentEqualToPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.DestinationEqualsPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.jgiven.GivenStage;

import java.util.ArrayList;
import java.util.Optional;

@JGivenStage
public class MessageClassificationGivenStage extends GivenStage<MessageClassificationGivenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public MessageClassificationGivenStage aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue(
            String content,
            String broker,
            String queue) {
        ArrayList<FailedMessagePredicate> predicates = new ArrayList<>();
        predicates.add(new ContentEqualToPredicate(content));
        if (!"any".equalsIgnoreCase(broker)) {
            predicates.add(new BrokerEqualsPredicate(broker));
        }
        if (!"any".equalsIgnoreCase(queue)) {
            predicates.add(new DestinationEqualsPredicate(Optional.of(queue)));
        }
        testRestTemplate.postForLocation(
                "/core/message-classification",
                new MessageClassifier("Default Classifier", new AndPredicate(predicates), new DeleteMessageAction(null))
        );
        return this;
    }

    public MessageClassificationGivenStage aMessageClassifierExistsToLabelAnyMessage$FromBroker$(String label, String broker) {
        testRestTemplate.postForLocation(
                "/core/message-classification",
                new MessageClassifier("Default Classifier", new BrokerEqualsPredicate(broker), new LabelMessageAction(label, null))
        );
        return this;
    }

    public MessageClassificationGivenStage theMessageClassificationJobIsNotRunning() {
        testRestTemplate.put(
                "/core/admin/executor/message-classification/pause",
                HttpEntity.EMPTY
        );
        return super.self();
    }

    public void noMessageClassifiersExist() {
        testRestTemplate.delete("/core/message-classification");
    }
}
