package uk.gov.dwp.queue.triage.core.remove;

import org.junit.Test;
import org.slf4j.Logger;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveFailedMessageServiceTest {

    private static final long NUMBER_OF_MESSAGES_REMOVED = 2;
    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);
    private final Logger logger = mock(Logger.class);

    private final RemoveFailedMessageService underTest = new RemoveFailedMessageService(failedMessageDao, logger);
    private final Exception exception = new RuntimeException();

    @Test
    public void removeDelegatesToTheDao() {
        when(failedMessageDao.removeFailedMessages()).thenReturn(NUMBER_OF_MESSAGES_REMOVED);

        underTest.removeFailedMessages();

        verify(failedMessageDao).removeFailedMessages();
    }

    @Test
    public void exceptionsAreSwallowedAndLogged() {
        when(failedMessageDao.removeFailedMessages()).thenThrow(exception);

        underTest.removeFailedMessages();

        verify(logger).error("Could not remove messages", exception);
    }
}