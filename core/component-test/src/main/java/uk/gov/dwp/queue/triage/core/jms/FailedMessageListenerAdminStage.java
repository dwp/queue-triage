package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.format.BooleanFormatter;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@JGivenStage
public class FailedMessageListenerAdminStage extends Stage<FailedMessageListenerAdminStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @ExpectedScenarioState
    private ResponseEntity<?> response;

    public FailedMessageListenerAdminStage theMessageListenerFor$Is$Running(String brokerName,
                                                                            @Format(value = BooleanFormatter.class, args = {"", "not"}) boolean isRunning) {
        aRequestIsMadeToGetTheStatusOfTheMessageListenerForBroker$(brokerName);
        assertThat(response.getBody(), is(isRunning));
        return this;
    }

    public FailedMessageListenerAdminStage aRequestIsMadeToStopTheMessageListenerForBroker$(String brokerName) {
        response = testRestTemplate.postForEntity(
                "/core/admin/jms-listener/stop/{brokerName}",
                HttpEntity.EMPTY,
                String.class,
                brokerName
        );
        return this;
    }

    public FailedMessageListenerAdminStage aRequestIsMadeToStartTheMessageListenerForBroker$(String brokerName) {
        response = testRestTemplate.postForEntity(
                "/core/admin/jms-listener/start/{brokerName}",
                HttpEntity.EMPTY,
                String.class,
                brokerName
        );
        return this;
    }

    public FailedMessageListenerAdminStage theMessageListenerAdminResourceRespondsWith$StatusCode(Response.Status status) {
        assertThat(response.getStatusCodeValue(), is(status.getStatusCode()));
        return this;
    }

    public FailedMessageListenerAdminStage aRequestIsMadeToGetTheStatusOfTheMessageListenerForBroker$(String brokerName) {
        response = testRestTemplate.getForEntity(
                "/core/admin/jms-listener/running/{brokerName}",
                Boolean.class,
                brokerName);
        return this;
    }
}
