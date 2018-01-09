package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.DBObject;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEventMatcher.equalTo;

public class StatusHistoryEventDBObjectConverterTest {

    private static final Instant NOW = Instant.now();

    private final FailedMessageStatusDBObjectConverter underTest = new FailedMessageStatusDBObjectConverter();

    @Test
    public void testConvertQueueToDBObjectAndBack() throws Exception {
        DBObject basicDBObject = underTest.convertFromObject(new StatusHistoryEvent(FAILED, NOW));

        assertThat(underTest.convertToObject(basicDBObject), is(equalTo(FAILED).withUpdatedDateTime(NOW)));
    }


}