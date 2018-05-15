package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.DestinationMatcher;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEventMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyMap;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.RESEND;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.SENT;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.statusHistoryEvent;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageMongoDaoTest extends AbstractMongoDaoTest {

    private static final Instant SIX_DAYS_AGO =  now().minus(6, DAYS);
    private static final Instant EIGHT_DAYS_AGO =  now().minus(8, DAYS);
    private static final String JMS_MESSAGE_ID = "jms-message-id";

    private final FailedMessageId failedMessageId = newFailedMessageId();
    private final FailedMessageBuilder failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
            .withFailedMessageId(failedMessageId)
            .withJmsMessageId(JMS_MESSAGE_ID)
            .withDestination(new Destination("broker", of("queue.name")))
            .withContent("Hello")
            .withSentDateTime(now())
            .withFailedDateTime(now());

    @Autowired
    private FailedMessageMongoDao underTest;

    @Test
    public void findFailedMessageThatDoesNotExistReturnsNull() {
        assertThat(underTest.findById(newFailedMessageId()), is(Optional.empty()));
    }

    @Test
    public void saveMessageWithEmptyPropertiesAndNoLabels() {
        failedMessageBuilder.withProperties(emptyMap());

        underTest.insert(failedMessageBuilder.build());

        assertThat(underTest.findById(failedMessageId).orElse(null), is(aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello"))
                .withProperties(equalTo(emptyMap()))
                .withFailedMessageStatus(StatusHistoryEventMatcher.equalTo(FAILED))
                .withLabels(emptyIterable())
        ));
    }

    @Test
    public void saveMessageWithPropertiesAndLabels() {
        Map<String, Object> props = ImmutableMap.<String, Object>builder()
                .put("string", "Builder")
                .put("localDateTime", LocalDateTime.now())
                .put("date", new Date())
                .put("integer", 1)
                .put("long", 1L)
                .put("uuid", UUID.randomUUID())
                .build();

        Map<String, Object> properties = new HashMap<>(props);
        properties.put("properties", new HashMap<>(props));
        failedMessageBuilder
                .withProperties(properties)
                .withLabel("foo")
                .withLabel("bar");

        underTest.insert(failedMessageBuilder.build());

        assertThat(underTest.findById(failedMessageId).orElse(null), is(aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello"))
                .withProperties(equalTo(properties))
                .withFailedMessageStatus(StatusHistoryEventMatcher.equalTo(FAILED))
                .withLabels(containsInAnyOrder("foo", "bar"))
        ));
    }

    @Test
    public void findNumberOfMessagesForBroker() {
        underTest.insert(failedMessageBuilder
                .withNewFailedMessageId()
                .withDestination(new Destination("brokerA", of("queue-name")))
                .build());
        underTest.insert(failedMessageBuilder
                .withNewFailedMessageId()
                .withDestination(new Destination("brokerB", of("queue-name")))
                .build());

        assertThat(underTest.findNumberOfMessagesForBroker("brokerA"), is(1L));
    }

    @Test
    public void findNumberOfMessagesForBrokerReturnsZeroWhenNoneExist() {
        underTest.insert(failedMessageBuilder
                .withNewFailedMessageId()
                .withDestination(new Destination("brokerB", of("queue-name")))
                .build());

        assertThat(underTest.findNumberOfMessagesForBroker("brokerA"), is(0L));
    }

    @Test
    public void updateStatus() {
        underTest.insert(failedMessageBuilder.build());
        underTest.update(failedMessageBuilder.withStatusHistoryEvent(statusHistoryEvent(RESEND)).build());

        assertThat(underTest.getStatusHistory(failedMessageId), contains(
                StatusHistoryEventMatcher.equalTo(RESEND),
                StatusHistoryEventMatcher.equalTo(FAILED)
        ));
    }

    @Test
    public void removeOnAnEmptyCollection() {
        assertThat(underTest.removeFailedMessages(), is(0L));
    }

    @Test
    public void removeWhenNoEligibleRecords() {
        underTest.insert(newFailedMessageWithStatus(FAILED, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(RESEND, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(SENT, EIGHT_DAYS_AGO));

        assertThat(underTest.removeFailedMessages(), is(0L));
        assertThat(collection.count(), is(3L));
    }

    @Test
    public void successfullyRemoveDeletedRecordsMoreThan7DaysOld() {
        underTest.insert(newFailedMessageWithStatus(FAILED, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(RESEND, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(SENT, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(DELETED, SIX_DAYS_AGO));

        underTest.insert(newFailedMessageWithStatus(DELETED, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(DELETED, EIGHT_DAYS_AGO));

        assertThat(underTest.removeFailedMessages(), is(2L));
        assertThat(collection.count(), is(4L));
    }

    @Test
    public void addLabelToAFailedMessage() {
        underTest.insert(failedMessageBuilder.build());
        underTest.addLabel(failedMessageId, "foo");

        assertThat(underTest.findById(failedMessageId).orElse(null), aFailedMessage().withLabels(contains("foo")));
    }

    @Test
    public void addDuplicateLabelToAFailedMessage() {
        underTest.insert(failedMessageBuilder.withLabel("foo").build());
        underTest.addLabel(failedMessageId, "foo");

        assertThat(underTest.findById(failedMessageId).orElse(null), aFailedMessage().withLabels(contains("foo")));
    }

    @Test
    public void addLabelToAFailedMessageThatDoesNotExist() {
        underTest.addLabel(failedMessageId, "foo");

        //TODO: What should be the behaviour (no current use case)? Throw an Exception?  Return a Boolean?
        assertThat(collection.count(), is(0L));
    }

    @Test
    public void setLabelsOnAFailedMessageReplacesExistingLabels() {
        underTest.insert(failedMessageBuilder.withLabel("something").build());

        underTest.setLabels(failedMessageId, ImmutableSet.of("foo", "bar"));

        assertThat(underTest.findById(failedMessageId).orElse(null), aFailedMessage().withLabels(containsInAnyOrder("foo", "bar")));
    }

    @Test
    public void removeLabelFromAFailedMessage() {
        underTest.insert(failedMessageBuilder.withLabel("foo").build());

        underTest.removeLabel(failedMessageId, "foo");

        assertThat(underTest.findById(failedMessageId).orElse(null), aFailedMessage().withLabels(emptyIterable()));
    }

    @Test
    public void removeLabelThatDoesNotExistFromAFailedMessageIsSuccessful() {
        underTest.insert(failedMessageBuilder.withLabel("foo").build());

        underTest.removeLabel(failedMessageId, "bar");

        assertThat(underTest.findById(failedMessageId).orElse(null), aFailedMessage().withLabels(contains("foo")));
    }

    @Test
    public void removeLabelForAFailedMessageThatDoesNotExist() {
        underTest.removeLabel(failedMessageId, "bar");

        // What should be the behaviour (no current use case)? Throw an Exception?  Return a Boolean?
        assertThat(collection.count(), is(0L));
    }

    @Test
    public void updateFailedMessage() {
        underTest.insert(failedMessageBuilder.build());

        underTest.update(failedMessageBuilder
                .withJmsMessageId("new-" + JMS_MESSAGE_ID)
                .withDestination(new Destination("broker", of("another.queue.name")))
                .withContent("Goodbye")
                .withProperty("some", "property")
                .withStatusHistoryEvent(RESEND)
                .build()
        );

        assertThat(underTest.findById(failedMessageId).orElse(null), aFailedMessage()
                .withJmsMessageId(equalTo("new-" + JMS_MESSAGE_ID))
                .withDestination(DestinationMatcher.aDestination().withBrokerName("broker").withName("another.queue.name"))
                .withContent(equalTo("Goodbye"))
                .withProperties(IsMapContaining.hasEntry("some", "property"))
                .withFailedMessageStatus(StatusHistoryEventMatcher.equalTo(RESEND))
        );
    }

    public FailedMessage newFailedMessageWithStatus(StatusHistoryEvent.Status status, Instant instant) {
        return failedMessageBuilder
                .withNewFailedMessageId()
                .withStatusHistoryEvent(new StatusHistoryEvent(status, instant))
                .build();
    }

    @Override
    protected String getCollectionName() {
        return mongoDaoProperties.getFailedMessage().getName();
    }
}