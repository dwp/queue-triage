package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.dao.util.HashMapBuilder;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
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
import static org.hamcrest.Matchers.nullValue;
import static uk.gov.dwp.queue.triage.core.dao.util.HashMapBuilder.newHashMap;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.RESEND;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.SENT;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.failedMessageStatus;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageMongoDaoTest extends AbstractMongoDaoTest {

    private static final Instant SIX_DAYS_AGO =  now().minus(6, DAYS);
    private static final Instant EIGHT_DAYS_AGO =  now().minus(8, DAYS);

    private final FailedMessageId failedMessageId = newFailedMessageId();
    private final FailedMessageBuilder failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
            .withFailedMessageId(failedMessageId)
            .withDestination(new Destination("broker", of("queue.name")))
            .withContent("Hello")
            .withSentDateTime(now())
            .withFailedDateTime(now());

    @Autowired
    private FailedMessageMongoDao underTest;

    @Test
    public void findFailedMessageThatDoesNotExistReturnsNull() {
        assertThat(underTest.findById(newFailedMessageId()), is(nullValue(FailedMessage.class)));
    }

    @Test
    public void saveMessageWithEmptyPropertiesAndNoLabels() throws Exception {
        failedMessageBuilder.withProperties(emptyMap());

        underTest.insert(failedMessageBuilder.build());

        assertThat(underTest.findById(failedMessageId), is(aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello"))
                .withProperties(equalTo(emptyMap()))
                .withFailedMessageStatus(FailedMessageStatusMatcher.equalTo(FAILED))
                .withLabels(emptyIterable())
        ));
    }

    @Test
    public void saveMessageWithPropertiesAndLabels() throws Exception {
        HashMapBuilder<String, Object> hashMapBuilder = newHashMap(String.class, Object.class)
                .put("string", "Builder")
                .put("localDateTime", LocalDateTime.now())
                .put("date", new Date())
                .put("integer", 1)
                .put("long", 1L)
                .put("uuid", UUID.randomUUID());

        HashMap<String, Object> properties = hashMapBuilder.build();
        properties.put("properties", hashMapBuilder.build());
        failedMessageBuilder
                .withProperties(properties)
                .withLabel("foo")
                .withLabel("bar");

        underTest.insert(failedMessageBuilder.build());

        assertThat(underTest.findById(failedMessageId), is(aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello"))
                .withProperties(equalTo(properties))
                .withFailedMessageStatus(FailedMessageStatusMatcher.equalTo(FAILED))
                .withLabels(containsInAnyOrder("foo", "bar"))
        ));
    }

    @Test
    public void findNumberOfMessagesForBroker() throws Exception {
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
    public void findNumberOfMessagesForBrokerReturnsZeroWhenNoneExist() throws Exception {
        underTest.insert(failedMessageBuilder
                .withNewFailedMessageId()
                .withDestination(new Destination("brokerB", of("queue-name")))
                .build());

        assertThat(underTest.findNumberOfMessagesForBroker("brokerA"), is(0L));
    }

    @Test
    public void updateStatus() throws Exception {
        underTest.insert(failedMessageBuilder.build());
        underTest.updateStatus(failedMessageId, failedMessageStatus(RESEND));

        assertThat(underTest.getStatusHistory(failedMessageId), contains(
                FailedMessageStatusMatcher.equalTo(RESEND),
                FailedMessageStatusMatcher.equalTo(FAILED)
        ));
    }

    @Test
    public void removeOnAnEmptyCollection() throws Exception {
        assertThat(underTest.removeFailedMessages(), is(0));
    }

    @Test
    public void removeWhenNoEligibleRecords() throws Exception {
        underTest.insert(newFailedMessageWithStatus(FAILED, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(RESEND, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(SENT, EIGHT_DAYS_AGO));

        assertThat(underTest.removeFailedMessages(), is(0));
        assertThat(collection.count(), is(3L));
    }

    @Test
    public void successfullyRemoveDeletedRecordsMoreThan7DaysOld() throws Exception {
        underTest.insert(newFailedMessageWithStatus(FAILED, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(RESEND, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(SENT, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(DELETED, SIX_DAYS_AGO));

        underTest.insert(newFailedMessageWithStatus(DELETED, EIGHT_DAYS_AGO));
        underTest.insert(newFailedMessageWithStatus(DELETED, EIGHT_DAYS_AGO));

        assertThat(underTest.removeFailedMessages(), is(2));
        assertThat(collection.count(), is(4L));
    }

    @Test
    public void addLabelToAFailedMessage() {
        underTest.insert(failedMessageBuilder.build());
        underTest.addLabel(failedMessageId, "foo");

        assertThat(underTest.findById(failedMessageId), aFailedMessage().withLabels(contains("foo")));
    }

    @Test
    public void addDuplicateLabelToAFailedMessage() {
        underTest.insert(failedMessageBuilder.withLabel("foo").build());
        underTest.addLabel(failedMessageId, "foo");

        assertThat(underTest.findById(failedMessageId), aFailedMessage().withLabels(contains("foo")));
    }

    @Test
    public void addLabelToAFailedMessageThatDoesNotExist() {
        underTest.addLabel(failedMessageId, "foo");

        //TODO: What should be the behaviour (no current use case)? Throw an Exception?  Return a Boolean?
        assertThat(collection.count(), is(0L));
    }

    @Test
    public void setLabelsOnAFailedMessageReplacesExistingLabels() throws Exception {
        underTest.insert(failedMessageBuilder.withLabel("something").build());

        underTest.setLabels(failedMessageId, ImmutableSet.of("foo", "bar"));

        assertThat(underTest.findById(failedMessageId), aFailedMessage().withLabels(containsInAnyOrder("foo", "bar")));
    }

    @Test
    public void removeLabelFromAFailedMessage() {
        underTest.insert(failedMessageBuilder.withLabel("foo").build());

        underTest.removeLabel(failedMessageId, "foo");

        assertThat(underTest.findById(failedMessageId), aFailedMessage().withLabels(emptyIterable()));
    }

    @Test
    public void removeLabelThatDoesNotExistFromAFailedMessageIsSuccessful() {
        underTest.insert(failedMessageBuilder.withLabel("foo").build());

        underTest.removeLabel(failedMessageId, "bar");

        assertThat(underTest.findById(failedMessageId), aFailedMessage().withLabels(contains("foo")));
    }

    @Test
    public void removeLabelForAFailedMessageThatDoesNotExist() {
        underTest.removeLabel(failedMessageId, "bar");

        //TODO: What should be the behaviour (no current use case)? Throw an Exception?  Return a Boolean?
        assertThat(collection.count(), is(0L));
    }

    public FailedMessage newFailedMessageWithStatus(FailedMessageStatus.Status status, Instant instant) {
        return failedMessageBuilder
                .withNewFailedMessageId()
                .withFailedMessageStatus(new FailedMessageStatus(status, instant))
                .build();
    }

    @Override
    protected String getCollectionName() {
        return mongoDaoProperties.getFailedMessage().getName();
    }
}