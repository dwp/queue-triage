package uk.gov.dwp.queue.triage.sample.data.create;

import com.fasterxml.jackson.databind.InjectableValues;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.CreateFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.time.Instant;
import java.util.UUID;

import static java.util.Collections.singletonList;

public class CreateFailedMessages {

    private final CreateFailedMessageClient createFailedMessageClient;

    public CreateFailedMessages(CreateFailedMessageClient createFailedMessageClient) {
        this.createFailedMessageClient = createFailedMessageClient;
    }

    public void createFailedMessages(int numberOfMessages,
                                     CreateFailedMessageRequestBuilder createFailedMessageRequestBuilder) {
        for (int i=0; i<numberOfMessages; i++) {
            createFailedMessageClient.create(createFailedMessageRequestBuilder
                    .withFailedMessageId(FailedMessageId.newFailedMessageId())
                    .withFailedDateTime(Instant.now())
                    .withContent("{ \"id\": \"" + UUID.randomUUID() + "\", \"action\": \"DELETE\" }")
                    .build()
            );
        }
    }

    public static void main(String[] args) {
        final JacksonConfiguration jacksonConfiguration = new JacksonConfiguration();
        final CreateFailedMessages createFailedMessages = new CreateFailedMessages(
                JAXRSClientFactory.create(
                        "http://localhost:9991/core",
                        CreateFailedMessageClient.class,
                        singletonList(jacksonConfiguration.jacksonJsonProvider(
                                new CxfConfiguration(), jacksonConfiguration.objectMapper(new InjectableValues.Std())
                        ))
                )
        );
        createFailedMessages.createFailedMessages(
                5,
                CreateFailedMessageRequest.newCreateFailedMessageRequest()
                        .withBrokerName("internal-broker")
                        .withDestinationName("core.queue")
                        .withProperty("traceId", UUID.randomUUID())
                        .withProperty("retries", 1)
        );
    }
}