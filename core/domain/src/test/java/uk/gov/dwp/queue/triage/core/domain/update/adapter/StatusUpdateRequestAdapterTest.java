package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import org.junit.Test;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEventMatcher;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;

public class StatusUpdateRequestAdapterTest {

    private static final Instant NOW = Instant.now();

    private final FailedMessageBuilder failedMessageBuilder = mock(FailedMessageBuilder.class);

    private final StatusUpdateRequestAdapter underTest = new StatusUpdateRequestAdapter();

    @Test
    public void statusHistoryEventIsSet() {
        underTest.adapt(new StatusUpdateRequest(FAILED, NOW), failedMessageBuilder);

        verify(failedMessageBuilder).withStatusHistoryEvent(
                argThat(new HamcrestArgumentMatcher<>(StatusHistoryEventMatcher.equalTo(FAILED).withUpdatedDateTime(NOW)))
        );
    }
}