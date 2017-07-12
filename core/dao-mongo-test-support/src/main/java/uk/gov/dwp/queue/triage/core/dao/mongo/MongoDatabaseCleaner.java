package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDatabaseCleaner {

    private final MongoClient mongoClient;

    public MongoDatabaseCleaner(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void cleanAllDatabases() {
        for (DB db : mongoClient.getUsedDatabases()) {
            if (!"admin".equals(db.getName())) {
                cleanCollections(db);
            }
        }
    }

    public void cleanCollections(DB db) {
        for (String collectionName : db.getCollectionNames()) {
            db.getCollection(collectionName).remove(new BasicDBObject());
        }
    }
}
