package uk.gov.dwp.queue.triage.core.resource.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.SENT;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryResponseMatcher.statusHistoryResponse;

public class FailedMessageStatusHistoryResourceTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private FailedMessageDao failedMessageDao;
    private FailedMessageStatusHistoryResource underTest;

    @Before
    public void setUp() {
        underTest = new FailedMessageStatusHistoryResource(failedMessageDao);
    }

    @Test
    public void failedMessageWithStatusHistoryEntries() {
        when(failedMessageDao.getStatusHistory(FAILED_MESSAGE_ID)).thenReturn(Arrays.asList(
                new StatusHistoryEvent(StatusHistoryEvent.Status.FAILED, NOW),
                new StatusHistoryEvent(StatusHistoryEvent.Status.SENT, NOW.plusSeconds(1))
        ));
        assertThat(underTest.getStatusHistory(FAILED_MESSAGE_ID), contains(
                statusHistoryResponse().withStatus(FAILED).withEffectiveDateTime(NOW),
                statusHistoryResponse().withStatus(SENT).withEffectiveDateTime(NOW.plusSeconds(1))
        ));
    }

    // FAILED & CLASSIFIED -> FAILED

    @Test
    public void responseOnlyContainsASingleEntryWhenFailedAndClassifiedStatusAreConcurrent() {
        when(failedMessageDao.getStatusHistory(FAILED_MESSAGE_ID)).thenReturn(Arrays.asList(
                new StatusHistoryEvent(StatusHistoryEvent.Status.FAILED, NOW),
                new StatusHistoryEvent(StatusHistoryEvent.Status.CLASSIFIED, NOW.plusSeconds(1))
        ));
        assertThat(underTest.getStatusHistory(FAILED_MESSAGE_ID), contains(
                statusHistoryResponse().withStatus(FAILED).withEffectiveDateTime(NOW.plusSeconds(1))
        ));
    }

    @Test
    public void singleStatusHistoryEntry() {
        when(failedMessageDao.getStatusHistory(FAILED_MESSAGE_ID)).thenReturn(Arrays.asList(
                new StatusHistoryEvent(StatusHistoryEvent.Status.CLASSIFIED, NOW)
        ));
        assertThat(underTest.getStatusHistory(FAILED_MESSAGE_ID), contains(
                statusHistoryResponse().withStatus(FAILED).withEffectiveDateTime(NOW)
        ));
    }
}