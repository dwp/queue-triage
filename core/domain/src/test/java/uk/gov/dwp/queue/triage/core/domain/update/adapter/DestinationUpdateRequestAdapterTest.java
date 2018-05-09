package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.update.DestinationUpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DestinationUpdateRequestAdapterTest {

    private static final String BROKER_NAME = "some-broker";
    private static final String DESTINATION_NAME = "some-destination";

    private final FailedMessageBuilder failedMessageBuilder = mock(FailedMessageBuilder.class);
    private final DestinationUpdateRequestAdapter underTest = new DestinationUpdateRequestAdapter();

    @Test
    public void verifyDestinationAdaptedCorrectly() {
        underTest.adapt(new DestinationUpdateRequest(BROKER_NAME, DESTINATION_NAME), failedMessageBuilder);

        verify(failedMessageBuilder).withDestination(new Destination(BROKER_NAME, Optional.of(DESTINATION_NAME)));
    }
}