package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.dao.util.HashMapBuilder;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static java.util.Collections.emptyMap;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static uk.gov.dwp.queue.triage.core.dao.util.HashMapBuilder.newHashMap;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.RESEND;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.failedMessageStatus;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageMongoDaoTest extends AbstractMongoDaoTest {

    private final FailedMessageId failedMessageId = newFailedMessageId();
    private final FailedMessageBuilder failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
            .withFailedMessageId(failedMessageId)
            .withDestination(new Destination("broker", of("queue.name")))
            .withContent("Hello")
            .withSentDateTime(Instant.now())
            .withFailedDateTime(Instant.now());

    @Autowired
    private FailedMessageMongoDao underTest;

    @Test
    public void findFailedMessageThatDoesNotExistReturnsNull() {
        assertThat(underTest.findById(newFailedMessageId()), is(nullValue(FailedMessage.class)));
    }

    @Test
    public void saveMessageWithEmptyProperties() throws Exception {
        failedMessageBuilder.withProperties(emptyMap());

        underTest.insert(failedMessageBuilder.build());

        assertThat(underTest.findById(failedMessageId), is(aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello"))
                .withProperties(equalTo(emptyMap()))
                .withFailedMessageStatus(FailedMessageStatusMatcher.equalTo(FAILED))
        ));
    }

    @Test
    public void saveMessageWithProperties() throws Exception {
        HashMapBuilder<String, Object> hashMapBuilder = newHashMap(String.class, Object.class)
                .put("string", "Builder")
                .put("localDateTime", LocalDateTime.now())
                .put("date", new Date())
                .put("integer", 1)
                .put("long", 1L)
                .put("uuid", UUID.randomUUID());

        HashMap<String, Object> properties = hashMapBuilder.build();
        properties.put("properties", hashMapBuilder.build());
        failedMessageBuilder.withProperties(properties);

        underTest.insert(failedMessageBuilder.build());

        assertThat(underTest.findById(failedMessageId), is(aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello"))
                .withProperties(equalTo(properties))
                .withFailedMessageStatus(FailedMessageStatusMatcher.equalTo(FAILED))
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

    @Override
    protected String getCollectionName() {
        return mongoDaoProperties.getFailedMessage().getName();
    }
}