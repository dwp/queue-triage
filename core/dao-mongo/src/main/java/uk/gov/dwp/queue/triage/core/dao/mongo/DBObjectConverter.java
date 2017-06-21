package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;

public interface DBObjectConverter<T> extends ObjectConverter<T, DBObject> {
}