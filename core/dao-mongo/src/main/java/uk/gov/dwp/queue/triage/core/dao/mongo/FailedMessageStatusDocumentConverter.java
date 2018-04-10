package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;

import java.time.Instant;

public class FailedMessageStatusDocumentConverter implements DocumentConverter<StatusHistoryEvent> {

    public static final String STATUS = "status";
    public static final String EFFECTIVE_DATE_TIME = "effectiveDateTime";
    public static final String LAST_MODIFIED_DATE_TIME = "_lastModifiedDateTime";

    @Override
    public StatusHistoryEvent convertToObject(Document document) {
        return new StatusHistoryEvent(
                StatusHistoryEvent.Status.valueOf(document.getString(STATUS)),
                document.get(EFFECTIVE_DATE_TIME, Instant.class)
        );
    }

    @Override
    public Document convertFromObject(StatusHistoryEvent item) {
        return new Document()
                .append(STATUS, item.getStatus().name())
                .append(EFFECTIVE_DATE_TIME, item.getEffectiveDateTime())
                .append(LAST_MODIFIED_DATE_TIME, Instant.now())
                ;
    }
}
