package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;

public interface DBObjectWithIdConverter<T, ID> extends DBObjectConverter<T> {

    BasicDBObject createId(ID id);
}