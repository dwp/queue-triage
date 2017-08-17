package uk.gov.dwp.queue.triage.web.server.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class FailedMessageListItemAdapterTest {

    private static final ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper();
    private static final Instant EPOCH = Instant.ofEpochMilli(0);
    private static final Instant SOME_DATE_TIME = Instant.from(ZonedDateTime.of(LocalDate.of(2016, 2, 8), LocalTime.of(14, 43, 0), UTC));
    private static final String FAILED_MESSAGE_ID_1 = UUID.randomUUID().toString();
    private static final String FAILED_MESSAGE_ID_2 = UUID.randomUUID().toString();
    private final FailedMessageListItemAdapter underTest = new FailedMessageListItemAdapter();

    @Test
    public void failedMessageWithNoProperties() throws JsonProcessingException {
        SearchFailedMessageResponse failedMessage1 = SearchFailedMessageResponse.newSearchFailedMessageResponse()
                .withFailedMessageId(FailedMessageId.fromString(FAILED_MESSAGE_ID_1))
                .withBroker("internal-broker")
                .withDestination(Optional.of("queue-name"))
                .withSentDateTime(EPOCH)
                .withFailedDateTime(SOME_DATE_TIME)
                .withContent("Some Content")
                .build();
        SearchFailedMessageResponse failedMessage2 = SearchFailedMessageResponse.newSearchFailedMessageResponse()
                .withFailedMessageId(FailedMessageId.fromString(FAILED_MESSAGE_ID_2))
                .withBroker("internal-broker")
                .withDestination(Optional.of("another-queue"))
                .withSentDateTime(SOME_DATE_TIME)
                .withFailedDateTime(SOME_DATE_TIME.with(MILLI_OF_SECOND, 123))
                .withContent("More Content")
                .build();

        String json = OBJECT_MAPPER.writeValueAsString(underTest.adapt(Arrays.asList(failedMessage1, failedMessage2)));

        assertThat(json, allOf(
                hasJsonPath("$.[0].recid", equalTo(FAILED_MESSAGE_ID_1)),
                hasJsonPath("$.[0].broker", equalTo("internal-broker")),
                hasJsonPath("$.[0].content", equalTo("Some Content")),
                hasJsonPath("$.[0].destination", equalTo("queue-name")),
                hasJsonPath("$.[0].sentDateTime", equalTo("1970-01-01T00:00:00.000Z")),
                hasJsonPath("$.[0].failedDateTime", equalTo("2016-02-08T14:43:00.000Z")),

                hasJsonPath("$.[1].recid", equalTo(FAILED_MESSAGE_ID_2)),
                hasJsonPath("$.[1].broker", equalTo("internal-broker")),
                hasJsonPath("$.[1].content", equalTo("More Content")),
                hasJsonPath("$.[1].destination", equalTo("another-queue")),
                hasJsonPath("$.[1].sentDateTime", equalTo("2016-02-08T14:43:00.000Z")),
                hasJsonPath("$.[1].failedDateTime", equalTo("2016-02-08T14:43:00.123Z"))
        ));
    }
}