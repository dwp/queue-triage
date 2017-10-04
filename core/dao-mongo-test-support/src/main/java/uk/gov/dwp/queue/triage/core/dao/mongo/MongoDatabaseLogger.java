package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MongoDatabaseLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDatabaseLogger.class);

    private final MongoClient mongoClient;

    public MongoDatabaseLogger(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void log(String databaseName) {
        LOGGER.info("Logging documents in {} started", databaseName);
        mongoClient
                .getDatabase(databaseName)
                .listCollectionNames()
                .forEach(logCollection(databaseName));
        LOGGER.info("Logging documents in {} complete", databaseName);
    }

    private Consumer<String> logCollection(String databaseName) {
        return collection -> mongoClient.getDatabase(databaseName)
                .getCollection(collection)
                .find()
                .forEach(logDocument(databaseName, collection));
    }

    private Consumer<Document> logDocument(String databaseName, String collection) {
        return document -> LoggerFactory.getLogger(databaseName + "." + collection).info(document.toJson());
    }

}
