package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;

public class FailedMessageBuilderTest {

    private static final String CONTENT = "Foo";
    private static final String BROKER_NAME = "broker";
    private static final String DESTINATION_NAME = "destination";
    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();
    private static final Instant FAILED_DATE_TIME = NOW.plusSeconds(1);
    private static final Instant SEND_DATE_TIME = NOW.minusSeconds(1);

    private FailedMessageBuilder underTest = FailedMessageBuilder.newFailedMessage()
            .withContent(CONTENT)
            .withDestination(new Destination(BROKER_NAME, Optional.of(DESTINATION_NAME)))
            .withFailedDateTime(FAILED_DATE_TIME)
            .withFailedMessageId(FAILED_MESSAGE_ID)
            .withStatusHistoryEvent(FAILED)
            .withSentDateTime(SEND_DATE_TIME);

    @Test
    public void addSingleLabelAndPropertyToBuilder() throws Exception {
        assertThat(
                underTest.withLabel("foo").withProperty("key", "value").build(),
                is(aFailedMessage()
                        .withLabels(Matchers.contains("foo"))
                        .withProperties(Matchers.hasEntry("key", "value"))
        ));
    }

    @Test
    public void createANewFailedMessageWithNoLabelsOrProperties() throws Exception {
        assertThat(
                underTest.build(),
                is(aFailedMessage()
                        .withLabels(is(Collections.emptySet()))
                        .withProperties(is(Collections.emptyMap()))
        ));
    }

    private FailedMessageMatcher aFailedMessage() {
        return FailedMessageMatcher.aFailedMessage()
                .withContent(equalTo(CONTENT))
                .withDestination(DestinationMatcher.aDestination().withBrokerName(BROKER_NAME).withName(DESTINATION_NAME))
                .withFailedAt(FAILED_DATE_TIME)
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withFailedMessageStatus(StatusHistoryEventMatcher.equalTo(FAILED))
                .withSentAt(SEND_DATE_TIME);
    }
}