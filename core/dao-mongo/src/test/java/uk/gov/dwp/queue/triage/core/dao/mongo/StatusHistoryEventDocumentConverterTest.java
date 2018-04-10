package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEventMatcher.equalTo;

public class StatusHistoryEventDocumentConverterTest {

    private static final Instant NOW = Instant.now();

    private final FailedMessageStatusDocumentConverter underTest = new FailedMessageStatusDocumentConverter();

    @Test
    public void testConvertQueueToDocumentAndBack() {
        Document document = underTest.convertFromObject(new StatusHistoryEvent(FAILED, NOW));

        assertThat(underTest.convertToObject(document), is(equalTo(FAILED).withUpdatedDateTime(NOW)));
    }


}