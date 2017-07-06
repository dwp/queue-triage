package uk.gov.dwp.queue.triage.core;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.resource.PersistedFailedMessageBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@JGivenStage
public class FailedMessageResourceStage extends Stage<FailedMessageResourceStage> {

    @Autowired
    @Qualifier("createFailedMessageClient")
    private CreateFailedMessageClient createFailedMessageClient;
    @Autowired
    @Qualifier("searchFailedMessageClient")
    private SearchFailedMessageClient searchFailedMessageClient;

    @ExpectedScenarioState
    private FailedMessageResponse failedMessage;

    public FailedMessageResourceStage aMessageWithId$IsSelected(FailedMessageId failedMessageId) {
        failedMessage = searchFailedMessageClient.getFailedMessage(failedMessageId);
        return this;
    }

    public FailedMessageResourceStage theNumberOfFailedMessagesForBroker$Is$(String brokerName, long numberOfMessages) {
        await().atMost(1, TimeUnit.SECONDS).until(() -> searchFailedMessageClient.getNumberOfFailedMessages(brokerName), is(numberOfMessages));
        return this;
    }

    public PersistedFailedMessageBuilder aFailedMessage() {
        return new PersistedFailedMessageBuilder(createFailedMessageClient);
    }

    public void thenTheFailedMessageReturned(FailedMessageResponseMatcher failedMessageResponseMatcher) {
        assertThat(failedMessage, failedMessageResponseMatcher);
    }

    public void noDataIsReturned() {
        assertThat(failedMessage, is(nullValue()));
    }
}
