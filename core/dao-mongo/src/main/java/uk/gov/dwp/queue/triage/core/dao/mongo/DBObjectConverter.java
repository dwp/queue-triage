package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;

import java.util.Collection;

public interface DBObjectConverter<T> extends ObjectConverter<T, DBObject> {

    static BasicDBList toBasicDBList(Collection<?> collection) {
        BasicDBList basicDBList = new BasicDBList();
        basicDBList.addAll(collection);
        return basicDBList;
    }

}