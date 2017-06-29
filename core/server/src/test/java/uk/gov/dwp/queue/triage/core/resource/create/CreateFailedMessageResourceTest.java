package uk.gov.dwp.queue.triage.core.resource.create;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateFailedMessageResourceTest {

    private final FailedMessageFactory failedMessageFactory = mock(FailedMessageFactory.class);
    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final CreateFailedMessageRequest request = mock(CreateFailedMessageRequest.class);

    private final CreateFailedMessageResource underTest = new CreateFailedMessageResource(failedMessageFactory, failedMessageDao);

    @Test
    public void createFailedMessage() throws Exception {
        when(failedMessageFactory.create(request)).thenReturn(failedMessage);

        underTest.create(request);

        verify(failedMessageDao).insert(failedMessage);
    }
}