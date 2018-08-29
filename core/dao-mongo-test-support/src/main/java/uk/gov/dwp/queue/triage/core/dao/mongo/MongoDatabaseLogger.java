package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.DocumentCodec;
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

    public void log(String databaseName, String collectionName) {
        LOGGER.info("Logging documents in {}.{} started", databaseName, collectionName);
        mongoClient
                .getDatabase(databaseName)
                .getCollection(collectionName)
                .find()
                .forEach(logDocument(databaseName, collectionName));
        LOGGER.info("Logging documents in {}.{} complete", databaseName, collectionName);
    }

    private Consumer<String> logCollection(String databaseName) {
        return collection -> mongoClient.getDatabase(databaseName)
                .getCollection(collection)
                .find()
                .forEach(logDocument(databaseName, collection));
    }

    private Consumer<Document> logDocument(String databaseName, String collection) {
        return document -> LoggerFactory.getLogger(databaseName + "." + collection)
                .info(document.toJson(new DocumentCodec(mongoClient.getMongoClientOptions().getCodecRegistry(), new BsonTypeClassMap())));
    }

}
