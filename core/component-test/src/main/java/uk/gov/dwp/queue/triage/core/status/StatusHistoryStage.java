package uk.gov.dwp.queue.triage.core.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@JGivenStage
public class StatusHistoryStage extends Stage<StatusHistoryStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @ExpectedScenarioState
    private ResponseEntity<List<StatusHistoryResponse>> response;

    public StatusHistoryStage theStatusHistoryIsRequestedFor(FailedMessageId failedMessageId) {
        response = testRestTemplate.exchange(
                "/core/failed-message/${failedMessageId}/status-history",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<StatusHistoryResponse>>() {},
                Collections.singletonMap("failedMessageId", failedMessageId)
        );
        return this;
    }

    public StatusHistoryStage theStatusHistory(Matcher<Iterable<? extends StatusHistoryResponse>> resultsMatcher) {
        assertThat(response.getStatusCodeValue(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.getBody(), resultsMatcher);
        return this;
    }
}
