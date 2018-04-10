package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDatabaseCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDatabaseCleaner.class);

    private final MongoClient mongoClient;

    public MongoDatabaseCleaner(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void cleanAllDatabases() {
        for (String dbName : mongoClient.listDatabaseNames()) {
            if (!"admin".equals(dbName)) {
                cleanDatabase(dbName);
            }
        }
    }

    public void cleanDatabase(String databaseName) {
        cleanCollections(mongoClient.getDatabase(databaseName));
    }

    public void cleanCollections(MongoDatabase db) {
        LOGGER.info("Removing data from '{}' database", db.getName());
        for (String collectionName : db.listCollectionNames()) {
            LOGGER.debug("Removing data from the '{}.{}' collection", db.getName(), collectionName);
            db.getCollection(collectionName).deleteMany(new Document());
        }
        LOGGER.info("Removing data from '{}' database complete", db.getName());
    }

    public void cleanCollection(String databaseName, String collectionName) {
        mongoClient.getDatabase(databaseName).getCollection(collectionName).deleteMany(new Document());
    }
}
