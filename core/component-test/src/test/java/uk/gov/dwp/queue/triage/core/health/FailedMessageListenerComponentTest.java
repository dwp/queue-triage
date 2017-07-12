package uk.gov.dwp.queue.triage.core.health;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.JmsStage;

public class FailedMessageListenerComponentTest extends BaseCoreComponentTest<JmsStage> {

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
