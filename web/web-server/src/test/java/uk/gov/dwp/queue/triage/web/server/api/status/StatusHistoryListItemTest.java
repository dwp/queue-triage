package uk.gov.dwp.queue.triage.web.server.api.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class StatusHistoryListItemTest {

    private static final ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper();
    private final StatusHistoryListItem underTest = new StatusHistoryListItem(new StatusHistoryResponse(FailedMessageStatus.FAILED, Instant.EPOCH.plusMillis(1)));

    @Test
    public void serialiseToJson() throws JsonProcessingException {
        final String json = OBJECT_MAPPER.writeValueAsString(underTest);
        assertThat(json, allOf(
                hasJsonPath("$.recid", equalTo("1-failed")),
                hasJsonPath("$.status", equalTo("Failed")),
                hasJsonPath("$.effectiveDateTime", equalTo("1970-01-01T00:00:00.001Z"))
        ));
    }

    @Test
    public void objectConstructedCorrectly() {

    }
}