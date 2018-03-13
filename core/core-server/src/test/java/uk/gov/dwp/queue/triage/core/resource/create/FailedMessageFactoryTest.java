package uk.gov.dwp.queue.triage.core.resource.create;

import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.DestinationMatcher;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;

public class FailedMessageFactoryTest {

    private static final Instant NOW = Instant.now();
    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private final FailedMessageFactory underTest = new FailedMessageFactory();

    @Test
    public void createFailedMessageFromRequest() throws Exception {
        FailedMessage failedMessage = underTest.create(newCreateFailedMessageRequest()
                .withContent("Hello World")
                .withBrokerName("broker")
                .withDestinationName("queue")
                .withFailedDateTime(NOW)
                .withFailedMessageId(FAILED_MESSAGE_ID)
                .withProperties(Collections.singletonMap("foo", "bar"))
                .withSentDateTime(NOW)
                .build());

        assertThat(failedMessage, aFailedMessage()
                .withContent(equalTo("Hello World"))
                .withDestination(DestinationMatcher.aDestination().withBrokerName("broker").withName("queue"))
                .withFailedAt(equalTo(NOW))
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withProperties(Matchers.hasEntry("foo", "bar"))
                .withSentAt(equalTo(NOW))
        );
    }
}