package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.DBObject;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusMatcher.equalTo;

public class FailedMessageStatusDBObjectConverterTest {

    private static final Instant NOW = Instant.now();

    private final FailedMessageStatusDBObjectConverter underTest = new FailedMessageStatusDBObjectConverter();

    @Test
    public void testConvertQueueToDBObjectAndBack() throws Exception {
        DBObject basicDBObject = underTest.convertFromObject(new FailedMessageStatus(FAILED, NOW));

        assertThat(underTest.convertToObject(basicDBObject), is(equalTo(FAILED).withUpdatedDateTime(NOW)));
    }


}