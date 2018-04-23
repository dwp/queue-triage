package uk.gov.dwp.queue.triage.web.server.w2ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class W2UIResponseTest {

    private static final ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper();
    private final W2UIResponse<String> underTest = W2UIResponse.success(Arrays.asList("foo", "bar"));

    @Test
    public void serialiseW2UIResponseWithSimpleType() throws JsonProcessingException {
        final String json = OBJECT_MAPPER.writeValueAsString(underTest);

        assertThat(json, allOf(
                hasJsonPath("$.status", equalTo("success")),
                hasJsonPath("$.total", equalTo(2)),
                hasJsonPath("$.records", contains("foo", "bar"))
        ));
    }

    @Test
    public void objectConstructedCorrectly() {
        assertEquals("success", underTest.getStatus());
        assertEquals(2, underTest.getTotal());
        assertEquals(Arrays.asList("foo", "bar"), underTest.getRecords());
    }
}