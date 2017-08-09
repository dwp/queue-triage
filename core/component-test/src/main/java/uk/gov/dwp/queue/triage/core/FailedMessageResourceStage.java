package uk.gov.dwp.queue.triage.core;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.CreateFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.resource.PersistedFailedMessageBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@JGivenStage
public class FailedMessageResourceStage extends Stage<FailedMessageResourceStage> {

    @Autowired
    @Qualifier("createFailedMessageClient")
    private CreateFailedMessageClient createFailedMessageClient;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @ExpectedScenarioState
    private FailedMessageResponse failedMessage;

    public FailedMessageResourceStage aMessageWithId$IsSelected(FailedMessageId failedMessageId) {
        failedMessage = testRestTemplate.getForObject(
                "/core/failed-message/{failedMessageId}",
                FailedMessageResponse.class,
                failedMessageId);
        return this;
    }

    /**
     * @deprecated Use {@link FailedMessageResourceStage#aFailedMessage(CreateFailedMessageRequestBuilder)} instead
     * @return
     */
    @Deprecated
    public PersistedFailedMessageBuilder aFailedMessage() {
        return new PersistedFailedMessageBuilder(createFailedMessageClient);
    }

    public PersistedFailedMessageBuilder aFailedMessage(
            @Format(value = ReflectionArgumentFormatter.class,
                    args = {"failedMessageId", "brokerName", "destinationName"}
            ) CreateFailedMessageRequestBuilder failedMessageBuilder) {
        return new PersistedFailedMessageBuilder(createFailedMessageClient, failedMessageBuilder);
    }

    // TODO: Remove 'then'
    public void thenTheFailedMessageReturned(FailedMessageResponseMatcher failedMessageResponseMatcher) {
        assertThat(failedMessage, failedMessageResponseMatcher);
    }

    public void noDataIsReturned() {
        assertThat(failedMessage, is(nullValue()));
    }
}
