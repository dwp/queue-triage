package uk.gov.dwp.queue.triage.core;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.ContentEqualToPredicate;
import uk.gov.dwp.queue.triage.core.stub.app.resource.StubMessageClassifierResource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

@JGivenStage
public class JmsStage extends Stage<JmsStage> {

    private static final Logger DEAD_LETTER_ACTION_LOGGER = getLogger("DeadLetterAction");
    private static final Logger DO_NOTHING_LOGGER = getLogger("DoNothingAction");
    @Autowired
    private JmsTemplate dummyAppJmsTemplate;
    @Autowired
    private StubMessageClassifierResource stubMessageClassifierResource;

    public JmsStage aMessageWithContent$WillDeadLetter(String content) {
        addMessageClassifierWithContent(
                content,
                failedMessage -> {
                    DEAD_LETTER_ACTION_LOGGER.info("Throwing Exception for Message with content: {}", content);
                    throw new RuntimeException("Content is " + failedMessage.getContent());
                }
        );
        return this;
    }

    public JmsStage aMessageWithContent$WillBeConsumedSuccessfully(String content) {
        addMessageClassifierWithContent(
                content,
                failedMessage -> DO_NOTHING_LOGGER.debug("Received Message with content: {}", content)
        );
        return this;
    }

    public void addMessageClassifierWithContent(String content, FailedMessageAction failedMessageAction) {
        stubMessageClassifierResource.addMessageClassifier(MessageClassifier
                .when(new ContentEqualToPredicate(content))
                .then(failedMessageAction)
        );
    }

    public JmsStage aMessageWithContent$IsSentTo$OnBroker$(String content, String destination, String brokerName) {
        dummyAppJmsTemplate.send(destination, session -> session.createTextMessage(content));
        return this;
    }

    public JmsStage deadLetterQueueContains$Messages(long numberOfMessages) {
        dummyAppJmsTemplate.browse("ActiveMQ.DLQ", (BrowserCallback<Void>) (session, browser) -> {
            long count = 0;
            while (browser.getEnumeration().hasMoreElements()) {
                browser.getEnumeration().nextElement();
                count++;
            }
            assertThat(count, Matchers.is(numberOfMessages));
            return null;
        });
        return this;
    }
}
