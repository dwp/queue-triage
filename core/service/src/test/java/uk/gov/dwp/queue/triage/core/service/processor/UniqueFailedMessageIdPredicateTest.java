package uk.gov.dwp.queue.triage.core.service.processor;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class UniqueFailedMessageIdPredicateTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = newFailedMessageId();

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);

    private final UniqueFailedMessageIdPredicate underTest = new UniqueFailedMessageIdPredicate(failedMessageDao);

    @Test
    public void predicateReturnsTrueWhenFailedMesageWithIdDoesNotExist() {
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(Optional.empty());

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void predicateReturnsFalseWhenFailedMessageWithIdExists() {
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(Optional.of(failedMessage));

        assertThat(underTest.test(failedMessage), is(false));
    }
}