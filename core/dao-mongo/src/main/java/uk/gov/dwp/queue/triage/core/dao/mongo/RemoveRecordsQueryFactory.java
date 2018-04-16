package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.QueryOperators;
import org.bson.Document;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDocumentConverter.EFFECTIVE_DATE_TIME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDocumentConverter.STATUS;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;

public class RemoveRecordsQueryFactory {

    public Document create() {
        return new Document()
                .append(STATUS_HISTORY + ".0." + STATUS, DELETED.name())
                .append(STATUS_HISTORY + ".0." + EFFECTIVE_DATE_TIME, new Document(QueryOperators.LT, now().minus(7, DAYS)));
    }
}
