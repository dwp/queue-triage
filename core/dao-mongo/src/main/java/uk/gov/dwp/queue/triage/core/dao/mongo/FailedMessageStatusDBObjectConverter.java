package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;

import java.time.Instant;

public class FailedMessageStatusDBObjectConverter implements DBObjectConverter<FailedMessageStatus> {

    public static final String STATUS = "status";
    public static final String UPDATED_DATE_TIME = "updatedDateTime";

    @Override
    public FailedMessageStatus convertToObject(DBObject dbObject) {
        return new FailedMessageStatus(
                FailedMessageStatus.Status.valueOf((String)dbObject.get(STATUS)),
                (Instant)dbObject.get(UPDATED_DATE_TIME)
        );
    }

    @Override
    public DBObject convertFromObject(FailedMessageStatus item) {
        return new BasicDBObject()
                .append(STATUS, item.getStatus().name())
                .append(UPDATED_DATE_TIME, item.getUpdatedDateTime());
    }
}
