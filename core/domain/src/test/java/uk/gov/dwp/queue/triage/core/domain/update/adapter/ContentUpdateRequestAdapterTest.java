package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.update.ContentUpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContentUpdateRequestAdapterTest {

    private final ContentUpdateRequestAdapter underTest = new ContentUpdateRequestAdapter();
    private final FailedMessageBuilder failedMessageBuilder = mock(FailedMessageBuilder.class);

    @Test
    public void verifyMessageContentAdaptedCorrectly() {
        underTest.adapt(new ContentUpdateRequest("some-content"), failedMessageBuilder);

        verify(failedMessageBuilder).withContent("some-content");
    }
}