package uk.gov.dwp.queue.triage.core.client.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageRequestMatcher.aSearchRequest;

public class SearchFailedMessageRequestTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module());

    @Test
    public void serialiseAndDeserialiseWithOptionalFieldsMissing() throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(
                SearchFailedMessageRequest.newSearchFailedMessageRequest()
                        .withBroker("broker")
                        .withStatus(FAILED)
                        .build());

        assertThat(OBJECT_MAPPER.readValue(json, SearchFailedMessageRequest.class), is(
                aSearchRequest()
                        .withBrokerMatcher(equalTo("broker"))
                        .withDestination(equalTo(Optional.empty()))
                        .withStatusMatcher(contains(FAILED))
        ));
    }

    @Test
    public void serialiseAndDeserialiseWithAllFieldsPopulated() throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(
                SearchFailedMessageRequest.newSearchFailedMessageRequest()
                        .withBroker("broker")
                        .withDestination("queue")
                        .withStatus(FAILED)
                        .build());

        assertThat(OBJECT_MAPPER.readValue(json, SearchFailedMessageRequest.class), is(
                aSearchRequest()
                        .withBrokerMatcher(equalTo("broker"))
                        .withDestination(equalTo(Optional.of("queue")))
                        .withStatusMatcher(contains(FAILED))
        ));
    }
}