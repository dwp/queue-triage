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
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponseMatcher;
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
        failedMessage = findFailedMessageById(failedMessageId);
        return this;
    }

    private FailedMessageResponse findFailedMessageById(FailedMessageId failedMessageId) {
        return testRestTemplate.getForObject(
                "/core/failed-message/{failedMessageId}",
                FailedMessageResponse.class,
                failedMessageId);
    }

    /**
     * @deprecated Use {@link FailedMessageResourceStage#aFailedMessage$Exists(CreateFailedMessageRequestBuilder)} instead
     * @return
     */
    @Deprecated
    public PersistedFailedMessageBuilder aFailedMessage() {
        return new PersistedFailedMessageBuilder(createFailedMessageClient);
    }

    public FailedMessageResourceStage aFailedMessage$Exists(
            @Format(value = ReflectionArgumentFormatter.class,
                    args = {"failedMessageId", "brokerName", "destinationName"}
            ) CreateFailedMessageRequestBuilder failedMessageBuilder) {
        new PersistedFailedMessageBuilder(createFailedMessageClient, failedMessageBuilder).exists();
        return this;
    }

    public void theFailedMessageReturned(FailedMessageResponseMatcher failedMessageResponseMatcher) {
        assertThat(failedMessage, failedMessageResponseMatcher);
    }

    public void noDataIsReturned() {
        assertThat(failedMessage, is(nullValue()));
    }
}
