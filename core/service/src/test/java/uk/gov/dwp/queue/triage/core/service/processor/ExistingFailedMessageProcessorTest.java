package uk.gov.dwp.queue.triage.core.service.processor;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExistingFailedMessageProcessorTest {

    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    private ExistingFailedMessageProcessor underTest = new ExistingFailedMessageProcessor(failedMessageDao);

    @Test
    public void successfullyProcessExistingFailedMessage() {
        underTest.process(failedMessage);

        verify(failedMessageDao).update(failedMessage);
    }
}