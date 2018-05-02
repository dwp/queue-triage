package uk.gov.dwp.queue.triage.web.component.status;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matcher;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ThenStage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@JGivenStage
public class StatusHistoryThenStage extends ThenStage<StatusHistoryThenStage> {

    @ProvidedScenarioState
    private ResponseEntity<String> statusHistoryListItemsResponse;

    public StatusHistoryThenStage theStatusHistoryResponseForFailedMessage(
            FailedMessageId failedMessageId,
            Matcher<Object> statusHistoryListItemMatcher) {
        assertThat(statusHistoryListItemsResponse.getStatusCodeValue(), is(200));
        assertThat(statusHistoryListItemsResponse.getBody(), statusHistoryListItemMatcher);
        return this;
    }


}
