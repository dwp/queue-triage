package uk.gov.dwp.queue.triage.core.jms;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.format.BooleanFormatter;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@JGivenStage
public class FailedMessageListenerAdminStage extends Stage<FailedMessageListenerAdminStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @ProvidedScenarioState
    private ResponseEntity<?> response;

    public FailedMessageListenerAdminStage theMessageListenerFor$Is$Running(String brokerName,
                                                                            @Format(value = BooleanFormatter.class, args = {"", "not"}) boolean isRunning) {
        assertThat(new FailedMessageListenerFixture(testRestTemplate)
                .statusOfListenerForBroker(brokerName)
                .getBody(), is(isRunning));
        return this;
    }

    public FailedMessageListenerAdminStage theMessageListenerAdminResourceRespondsWith$StatusCode(Response.Status status) {
        assertThat(response.getStatusCodeValue(), is(status.getStatusCode()));
        return this;
    }
}
