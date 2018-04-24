package uk.gov.dwp.queue.triage.core.search;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class SearchFailedMessageResponseAdapterTest {

    private static final Instant NOW = Instant.now();
    private static final String BROKER_NAME = "broker-name";
    private static final String DESTINATION_NAME = "destination-name";
    private static final String SOME_CONTENT = "some-content";
    private static final String JMS_MESSAGE_ID = "jms-message-id";
    private static final FailedMessageId FAILED_MESSAGE_ID = newFailedMessageId();

    private final FailedMessageBuilder failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
            .withContent(SOME_CONTENT)
            .withFailedMessageId(FAILED_MESSAGE_ID)
            .withJmsMessageId(JMS_MESSAGE_ID)
            .withProperties(Collections.singletonMap("foo", "bar"))
            .withLabel("foo")
            .withStatusHistoryEvent(new StatusHistoryEvent(FAILED, NOW));

    private final SearchFailedMessageResponseAdapter underTest = new SearchFailedMessageResponseAdapter();

    @Test
    public void destinationNameIsEmpty() {
        assertThat(underTest.toResponse(failedMessageBuilder.withDestination(new Destination(BROKER_NAME, Optional.empty())).build()), is(aFailedMessage()
                .withBroker(equalTo(BROKER_NAME))
                .withContent(equalTo(SOME_CONTENT))
                .withDestination(equalTo(Optional.empty()))
                .withStatus(FailedMessageStatus.FAILED)
                .withStatusDateTime(NOW)
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withJmsMessageId(equalTo(JMS_MESSAGE_ID))
                .withLabels(contains("foo"))
        ));
    }

    @Test
    public void destinationNameIsPopulated() {
        assertThat(underTest.toResponse(failedMessageBuilder.withDestination(new Destination(BROKER_NAME, Optional.of(DESTINATION_NAME))).build()), is(aFailedMessage()
                .withBroker(equalTo(BROKER_NAME))
                .withContent(equalTo(SOME_CONTENT))
                .withDestination(equalTo(Optional.of(DESTINATION_NAME)))
                .withStatus(FailedMessageStatus.FAILED)
                .withStatusDateTime(NOW)
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withJmsMessageId(equalTo(JMS_MESSAGE_ID))
                .withLabels(contains("foo"))
        ));
    }
}