package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier.MessageClassifierBuilder;
import uk.gov.dwp.queue.triage.core.classification.action.DeleteMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.BrokerEqualsPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.ContentEqualToPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.DestinationEqualsPredicate;

import java.util.Optional;

@JGivenStage
public class MessageClassificationGivenStage extends Stage<MessageClassificationGivenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public MessageClassificationGivenStage aMessageClassifierExistsToDeleteMessagesWithContent$On$BrokerAnd$Queue(
            String content,
            String broker,
            String queue) {
        MessageClassifierBuilder predicateBuilder = MessageClassifier.when(new ContentEqualToPredicate(content));
        if (!"any".equalsIgnoreCase(broker)) {
            predicateBuilder.and(new BrokerEqualsPredicate(broker));
        }
        if (!"any".equalsIgnoreCase(broker)) {
            predicateBuilder.and(new DestinationEqualsPredicate(Optional.of(queue)));
        }
        testRestTemplate.postForObject(
                "/core/message-classification",
                predicateBuilder.then(new DeleteMessageAction(null)),
                String.class
        );
        return this;
    }
}
