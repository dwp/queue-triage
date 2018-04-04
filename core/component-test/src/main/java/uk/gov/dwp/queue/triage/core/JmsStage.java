package uk.gov.dwp.queue.triage.core;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.ContentEqualToPredicate;
import uk.gov.dwp.queue.triage.core.jms.TextMessageBuilder;
import uk.gov.dwp.queue.triage.core.stub.app.resource.StubMessageClassifierResource;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@JGivenStage
public class JmsStage extends Stage<JmsStage> {

    private static final Logger LOGGER = getLogger(JmsStage.class);

    @Autowired
    private JmsTemplate dummyAppJmsTemplate;
    @Autowired
    private StubMessageClassifierResource stubMessageClassifierResource;

    public JmsStage aMessageWithContent$WillDeadLetter(String content) {
        addMessageClassifierWithContent(
                content,
                failedMessage -> {
                    getLogger("DeadLetterAction").info("Throwing Exception for Message with content: {}", content);
                    throw new RuntimeException("Content is " + failedMessage.getContent());
                }
        );
        return this;
    }

    public JmsStage aMessageWithContent$WillBeConsumedSuccessfully(String content) {
        addMessageClassifierWithContent(
                content,
                failedMessage -> getLogger("DoNothingAction").debug("Received Message with content: {}", content)
        );
        return this;
    }

    private void addMessageClassifierWithContent(String content, FailedMessageAction failedMessageAction) {
        stubMessageClassifierResource.addMessageClassifier(new MessageClassifier(
                new ContentEqualToPredicate(content), failedMessageAction)
        );
    }

    public JmsStage aMessageWithContent$IsSentTo$OnBroker$(String content, String destination, String brokerName) {
        dummyAppJmsTemplate.send(destination, session -> session.createTextMessage(content));
        return this;
    }

    public JmsStage aMessage$IsSentTo$OnBroker$(TextMessageBuilder textMessageBuilder, String destination, String broker) {
        dummyAppJmsTemplate.send(destination, session -> textMessageBuilder.build(session));
        return this;
    }

    public JmsStage deadLetterQueueStillContains$Messages(long numberOfMessages) {
        return deadLetterQueueContains$Messages(numberOfMessages);
    }

    public JmsStage deadLetterQueueContains$Messages(long numberOfMessages) {
        LOGGER.debug("Checking number of messages on ActiveMQ.DLQ");
        Awaitility.await()
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> dummyAppJmsTemplate.browse("ActiveMQ.DLQ", (session, browser) -> {
                    long count = 0;
                    while (browser.getEnumeration().hasMoreElements()) {
                        browser.getEnumeration().nextElement();
                        count++;
                    }
                    return count;
                }), Matchers.equalTo(numberOfMessages));
        return this;
    }
}
