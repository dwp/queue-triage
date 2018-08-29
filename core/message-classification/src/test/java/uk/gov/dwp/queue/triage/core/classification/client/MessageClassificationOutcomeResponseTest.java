package uk.gov.dwp.queue.triage.core.classification.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseBuilder.newFailedMessage;

public class MessageClassificationOutcomeResponseTest {

    private final FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();
    private final ObjectMapper objectMapper = JacksonConfiguration.defaultObjectMapper();

    @Test
    public void serialiseAndDeserialiseResponse() throws IOException {
        final FailedMessageResponse failedMessageResponse = newFailedMessage().withFailedMessageId(failedMessageId).build();

        final MessageClassificationOutcomeResponse outcomeResponse = objectMapper.readValue(
                objectMapper.writeValueAsString(new MessageClassificationOutcomeResponse(Boolean.FALSE, "Some Description", failedMessageResponse, "Some Action")),
                MessageClassificationOutcomeResponse.class
        );

        assertThat(outcomeResponse.isMatched(), is(Boolean.FALSE));
        assertThat(outcomeResponse.getDescription(), is("Some Description"));
        assertThat(outcomeResponse.getFailedMessageResponse(), is(aFailedMessage().withFailedMessageId(equalTo(failedMessageId))));
        assertThat(outcomeResponse.getAction(), is("Some Action"));

    }
}