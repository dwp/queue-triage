package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;

import java.time.Instant;

public class FailedMessageStatusDBObjectConverter implements DBObjectConverter<StatusHistoryEvent> {

    public static final String STATUS = "status";
    public static final String EFFECTIVE_DATE_TIME = "effectiveDateTime";
    public static final String LAST_MODIFIED_DATE_TIME = "_lastModifiedDateTime";

    @Override
    public StatusHistoryEvent convertToObject(DBObject dbObject) {
        return new StatusHistoryEvent(
                StatusHistoryEvent.Status.valueOf((String)dbObject.get(STATUS)),
                (Instant)dbObject.get(EFFECTIVE_DATE_TIME)
        );
    }

    @Override
    public DBObject convertFromObject(StatusHistoryEvent item) {
        return new BasicDBObject()
                .append(STATUS, item.getStatus().name())
                .append(EFFECTIVE_DATE_TIME, item.getEffectiveDateTime())
                .append(LAST_MODIFIED_DATE_TIME, Instant.now())
                ;
    }
}
