package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

@JGivenStage
public class FailedMessageListenerAdminWhenStage extends WhenStage<FailedMessageListenerAdminWhenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @ExpectedScenarioState
    private ResponseEntity<?> response;

    public FailedMessageListenerAdminWhenStage aRequestIsMadeToStopTheMessageListenerForBroker$(String brokerName) {
        response = new FailedMessageListenerFixture(testRestTemplate).stopListenerForBroker(brokerName);
        return this;
    }

    public FailedMessageListenerAdminWhenStage aRequestIsMadeToStartTheMessageListenerForBroker$(String brokerName) {
        response = new FailedMessageListenerFixture(testRestTemplate).startListenerForBroker(brokerName);
        return this;
    }

    public FailedMessageListenerAdminWhenStage aRequestIsMadeToGetTheStatusOfTheMessageListenerForBroker$(String brokerName) {
        response = new FailedMessageListenerFixture(testRestTemplate).statusOfListenerForBroker(brokerName);
        return this;
    }

    public FailedMessageListenerAdminWhenStage theMesageListenerReadsMessagesOnBroker$(String brokerName) {
        new FailedMessageListenerFixture(testRestTemplate).readMessagesForBroker(brokerName);
        return self();
    }
}
