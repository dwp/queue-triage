package uk.gov.dwp.queue.triage.core.resend;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HistoricStatusPredicateTest {

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final StatusHistoryEvent statusHistoryEvent = mock(StatusHistoryEvent.class);

    private final HistoricStatusPredicate underTest = new HistoricStatusPredicate();

    @Test
    public void effectiveDateTimeInPastReturnsTrue() throws Exception {
        when(failedMessage.getStatusHistoryEvent()).thenReturn(statusHistoryEvent);
        when(statusHistoryEvent.getEffectiveDateTime()).thenReturn(Instant.now().minus(1, MILLIS));

        assertThat(underTest.test(failedMessage), is(true));
    }

    @Test
    public void effectiveDateTimeInFutureReturnsFalse() throws Exception {
        when(failedMessage.getStatusHistoryEvent()).thenReturn(statusHistoryEvent);
        when(statusHistoryEvent.getEffectiveDateTime()).thenReturn(Instant.now().plus(1, SECONDS));

        assertThat(underTest.test(failedMessage), is(false));
    }
}