package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBList;
import org.bson.Document;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;

import java.util.Collection;

public interface DocumentConverter<T> extends ObjectConverter<T, Document> {

    static BasicDBList toBasicDBList(Collection<?> collection) {
        BasicDBList basicDBList = new BasicDBList();
        basicDBList.addAll(collection);
        return basicDBList;
    }

}