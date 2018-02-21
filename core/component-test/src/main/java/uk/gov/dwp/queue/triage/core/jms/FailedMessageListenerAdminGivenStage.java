package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import uk.gov.dwp.queue.triage.jgiven.GivenStage;

@JGivenStage
public class FailedMessageListenerAdminGivenStage extends GivenStage<FailedMessageListenerAdminGivenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public FailedMessageListenerAdminGivenStage theMesageListenerIsStoppedForBroker$(String brokerName) {
        new FailedMessageListenerFixture(testRestTemplate).stopListenerForBroker(brokerName);
        return self();
    }

    public FailedMessageListenerAdminGivenStage theMesageListenerIsRunningForBroker$(String brokerName) {
        new FailedMessageListenerFixture(testRestTemplate).startListenerForBroker(brokerName);
        return self();
    }
}
