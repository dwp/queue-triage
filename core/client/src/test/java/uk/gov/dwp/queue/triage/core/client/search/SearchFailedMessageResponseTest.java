package uk.gov.dwp.queue.triage.core.client.search;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.SearchFailedMessageResponseBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class SearchFailedMessageResponseTest {

    private static final Instant NOW = Instant.now();
    private static final String BROKER_NAME = "broker-name";
    private static final String DESTINATION_NAME = "destination-name";
    private static final String SOME_CONTENT = "some-content";
    private static final String JMS_MESSAGE_ID = "jms-message-id";
    private static final FailedMessageId FAILED_MESSAGE_ID = newFailedMessageId();
    private static final ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper(new InjectableValues.Std());

    private final SearchFailedMessageResponseBuilder searchFailedMessageResponseBuilder = newSearchFailedMessageResponse()
            .withBroker(BROKER_NAME)
            .withContent(SOME_CONTENT)
            .withFailedMessageId(FAILED_MESSAGE_ID)
            .withJmsMessageId(JMS_MESSAGE_ID)
            .withStatus(FailedMessageStatus.FAILED)
            .withStatusDateTime(NOW);

    @Test
    public void serialiseAndDeserialiseObjectWithDefaults() throws IOException {
        final String json = OBJECT_MAPPER.writeValueAsString(searchFailedMessageResponseBuilder
                .build());

        assertThat(OBJECT_MAPPER.readValue(json, SearchFailedMessageResponse.class), is(aFailedMessage()
                .withBroker(equalTo(BROKER_NAME))
                .withContent(equalTo(SOME_CONTENT))
                .withDestination(equalTo(Optional.empty()))
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withJmsMessageId(equalTo(JMS_MESSAGE_ID))
                .withStatus(FailedMessageStatus.FAILED)
                .withStatusDateTime(NOW)
                .withLabels(emptyIterable())
        ));
    }

    @Test
    public void serialiseAndDeserialiseObject() throws IOException {
        final String json = OBJECT_MAPPER.writeValueAsString(searchFailedMessageResponseBuilder
                .withDestination(DESTINATION_NAME)
                .withLabels(Collections.singleton("foo"))
                .build());

        assertThat(OBJECT_MAPPER.readValue(json, SearchFailedMessageResponse.class), is(aFailedMessage()
                .withBroker(equalTo(BROKER_NAME))
                .withContent(equalTo(SOME_CONTENT))
                .withDestination(equalTo(Optional.of(DESTINATION_NAME)))
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withJmsMessageId(equalTo(JMS_MESSAGE_ID))
                .withStatus(FailedMessageStatus.FAILED)
                .withStatusDateTime(NOW)
                .withLabels(contains("foo"))
        ));
    }
}