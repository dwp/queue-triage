package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;

import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder.newFailedMessage;

public class FailedMessageResponseFactoryTest {

    private static final Instant NOW = Instant.now();
    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    private final FailedMessageResponseFactory underTest = new FailedMessageResponseFactory();

    @Test
    public void createFailedMessageResponseFromAFailedMessage() {

        FailedMessageResponse failedMessageResponse = underTest.create(newFailedMessage()
                .withContent("Hello World")
                .withDestination(new Destination("broker", of("queue")))
                .withFailedDateTime(NOW)
                .withFailedMessageId(FAILED_MESSAGE_ID)
                .withProperties(Collections.singletonMap("foo", "bar"))
                .withSentDateTime(NOW)
                .build()
        );

        assertThat(failedMessageResponse, CoreMatchers.is(aFailedMessage()
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withContent(equalTo("Hello World"))
                .withBroker(equalTo("broker"))
                .withDestination(equalTo(of("queue")))
                .withFailedAt(equalTo(NOW))
                .withProperties(Matchers.hasEntry("foo", "bar"))
                .withSentAt(equalTo(NOW))));

    }
}