package uk.gov.dwp.queue.triage.web.component.status;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

import java.util.Collections;
import java.util.Objects;

@JGivenStage
public class StatusHistoryWhenStage extends WhenStage<StatusHistoryWhenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @ProvidedScenarioState
    private HttpHeaders httpHeaders;
    @ExpectedScenarioState
    private ResponseEntity<String> statusHistoryListItemsResponse;

    public StatusHistoryWhenStage theStatusHistoryIsRequestedForFailedMessage$(FailedMessageId failedMessageId) {
        Objects.requireNonNull(httpHeaders, "httpHeaders are not set.  Try executing LoginApiGivenStage#theUserHasSuccessfullyLoggedOn()");
        statusHistoryListItemsResponse = testRestTemplate.exchange(
                "/web/api/failed-messages/status-history/{failedMessageId}",
                HttpMethod.POST,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<String>() {},
                Collections.singletonMap("failedMessageId", failedMessageId));
        return self();
    }
}
