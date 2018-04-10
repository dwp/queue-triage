package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.QueryOperators;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DocumentMatcher.hasField;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDocumentConverter.EFFECTIVE_DATE_TIME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDocumentConverter.STATUS;

public class RemoveRecordsQueryFactoryTest {

    private RemoveRecordsQueryFactory underTest = new RemoveRecordsQueryFactory();

    @Test
    public void removeRecordsQueryCreatedSuccessfully() {
        assertThat(underTest.create(), allOf(
                hasField(STATUS_HISTORY + ".0." + STATUS, equalTo(StatusHistoryEvent.Status.DELETED.name())),
                hasField(STATUS_HISTORY + ".0." + EFFECTIVE_DATE_TIME, hasField(QueryOperators.LT, notNullValue(Instant.class)))
        ));
    }
}