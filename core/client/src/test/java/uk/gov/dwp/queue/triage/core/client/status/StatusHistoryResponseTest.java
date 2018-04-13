package uk.gov.dwp.queue.triage.core.client.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryResponseMatcher.statusHistoryResponse;

public class StatusHistoryResponseTest {

    private static final Instant NOW = Instant.now();
    private static final ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper();

    private final StatusHistoryResponse underTest = new StatusHistoryResponse(FAILED, NOW);

    @Test
    public void statusHistoryResponseTest() {
        assertThat(underTest, is(statusHistoryResponse().withStatus(FAILED).withEffectiveDateTime(NOW)));
    }

    @Test
    public void serialiseAndDeserialiseObject() throws IOException {
        final String json = OBJECT_MAPPER.writeValueAsString(underTest);
        assertThat(OBJECT_MAPPER.readValue(json, StatusHistoryResponse.class), is(statusHistoryResponse().withStatus(FAILED).withEffectiveDateTime(NOW)));
    }
}