package uk.gov.dwp.queue.triage.core.health;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.JmsStage;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class FailedMessageListenerComponentTest extends SimpleSpringRuleScenarioTest<JmsStage> {

    @ScenarioStage
    private FailedMessageResourceStage failedMessageResourceStage;

    @Test
    public void deadLetteredMessageIsProcessedByTheApplication() throws Exception {
        given().anApplicationIsListeningTo$OnBroker$("some-queue", "internal-broker");
        failedMessageResourceStage.and().theNumberOfFailedMessagesForBroker$Is$("internal-broker", 0L);

        when().anInvalidMessageIsSentTo$OnBroker$("some-queue", "internal-broker");

        failedMessageResourceStage.then().theNumberOfFailedMessagesForBroker$Is$("internal-broker", 1);
    }
}
