package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDBObjectConverter.STATUS;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDBObjectConverter.EFFECTIVE_DATE_TIME;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;

public class RemoveRecordsQueryFactory {

    public BasicDBObject create() {
        return new BasicDBObject()
                .append(STATUS_HISTORY + ".0." + STATUS, DELETED.name())
                .append(STATUS_HISTORY + ".0." + EFFECTIVE_DATE_TIME, new BasicDBObject(QueryOperators.LT, now().minus(7, DAYS)));
    }
}
