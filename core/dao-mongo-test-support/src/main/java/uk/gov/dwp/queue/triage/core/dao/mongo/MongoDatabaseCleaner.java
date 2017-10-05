package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDatabaseCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDatabaseCleaner.class);

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

    public void cleanDatabase(String databaseName) {
        cleanCollections(mongoClient.getDB(databaseName));
    }

    public void cleanCollections(DB db) {
        LOGGER.info("Removing data from '{}' database", db.getName());
        for (String collectionName : db.getCollectionNames()) {
            LOGGER.debug("Removing data from the '{}.{}' collection", db.getName(), collectionName);
            db.getCollection(collectionName).remove(new BasicDBObject());
        }
        LOGGER.info("Removing data from '{}' database complete", db.getName());
    }
}
