package uk.gov.dwp.queue.triage.core.resource.update;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.client.update.FailedMessageUpdateRequest;
import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Collections;

import static org.mockito.Mockito.verify;

public class UpdateFailedMessageResourceTest {

    private static FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private UpdateRequest updateRequest;
    @Mock
    private FailedMessageService failedMessageService;
    @InjectMocks
    private UpdateFailedMessageResource underTest;

    @Test
    public void resourceDelegatesToTheFailedMessageService() {
        underTest.update(FAILED_MESSAGE_ID, FailedMessageUpdateRequest.aNewFailedMessageUpdateRequest().withUpdateRequest(updateRequest).build());

        verify(failedMessageService).update(FAILED_MESSAGE_ID, Collections.singletonList(updateRequest));
    }
}