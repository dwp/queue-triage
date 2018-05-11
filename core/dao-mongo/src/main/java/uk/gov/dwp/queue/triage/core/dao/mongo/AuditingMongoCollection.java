package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.RenameCollectionOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class AuditingMongoCollection implements MongoCollection<Document> {

    static final String AUDIT_ACTION_KEY = "auditAction";
    static final String AUDIT_DATE_TIME_KEY = "auditDateTime";
    static final String DOCUMENT_KEY = "document";

    static final String DELETE_AUDIT_ACTION = "DELETE";
    static final String INSERT_AUDIT_ACTION = "INSERT";
    static final String REPLACE_AUDIT_ACTION = "REPLACE";
    static final String UPDATE_AUDIT_ACTION = "UPDATE";

    private static Logger LOGGER = LoggerFactory.getLogger(AuditingMongoCollection.class);

    private final MongoCollection<Document> failedMessageCollection;
    private final MongoCollection<Document> failedMessageAuditCollection;

    public AuditingMongoCollection(MongoCollection<Document> failedMessageCollection,
                                   MongoCollection<Document> failedMessageAuditCollection) {
        this.failedMessageCollection = failedMessageCollection;
        this.failedMessageAuditCollection = failedMessageAuditCollection;
    }

    @Override
    public MongoNamespace getNamespace() {
        return failedMessageCollection.getNamespace();
    }

    @Override
    public Class<Document> getDocumentClass() {
        return failedMessageCollection.getDocumentClass();
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return failedMessageCollection.getCodecRegistry();
    }

    @Override
    public ReadPreference getReadPreference() {
        return failedMessageCollection.getReadPreference();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return failedMessageCollection.getWriteConcern();
    }

    @Override
    public ReadConcern getReadConcern() {
        return failedMessageCollection.getReadConcern();
    }

    @Override
    public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> clazz) {
        return failedMessageCollection.withDocumentClass(clazz);
    }

    @Override
    public MongoCollection<Document> withCodecRegistry(CodecRegistry codecRegistry) {
        return failedMessageCollection.withCodecRegistry(codecRegistry);
    }

    @Override
    public MongoCollection<Document> withReadPreference(ReadPreference readPreference) {
        return failedMessageCollection.withReadPreference(readPreference);
    }

    @Override
    public MongoCollection<Document> withWriteConcern(WriteConcern writeConcern) {
        return failedMessageCollection.withWriteConcern(writeConcern);
    }

    @Override
    public MongoCollection<Document> withReadConcern(ReadConcern readConcern) {
        return failedMessageCollection.withReadConcern(readConcern);
    }

    @Override
    public long count() {
        return failedMessageCollection.count();
    }

    @Override
    public long count(Bson filter) {
        return failedMessageCollection.count(filter);
    }

    @Override
    public long count(Bson filter, CountOptions options) {
        return failedMessageCollection.count(filter, options);
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Class<TResult> tResultClass) {
        return failedMessageCollection.distinct(fieldName, tResultClass);
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Bson filter, Class<TResult> tResultClass) {
        return failedMessageCollection.distinct(fieldName, filter, tResultClass);
    }

    @Override
    public FindIterable<Document> find() {
        return failedMessageCollection.find();
    }

    @Override
    public <TResult> FindIterable<TResult> find(Class<TResult> tResultClass) {
        return failedMessageCollection.find(tResultClass);
    }

    @Override
    public FindIterable<Document> find(Bson filter) {
        return failedMessageCollection.find(filter);
    }

    @Override
    public <TResult> FindIterable<TResult> find(Bson filter, Class<TResult> tResultClass) {
        return failedMessageCollection.find(filter, tResultClass);
    }

    @Override
    public AggregateIterable<Document> aggregate(List<? extends Bson> pipeline) {
        return failedMessageCollection.aggregate(pipeline);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return failedMessageCollection.aggregate(pipeline, tResultClass);
    }

    @Override
    public MapReduceIterable<Document> mapReduce(String mapFunction, String reduceFunction) {
        return failedMessageCollection.mapReduce(mapFunction, reduceFunction);
    }

    @Override
    public <TResult> MapReduceIterable<TResult> mapReduce(String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
        return failedMessageCollection.mapReduce(mapFunction, reduceFunction, tResultClass);
    }

    @Override
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends Document>> requests) {
        LOGGER.warn("bulkWrite operation is not currently audited");
        return failedMessageCollection.bulkWrite(requests);
    }

    @Override
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends Document>> requests, BulkWriteOptions options) {
        LOGGER.warn("bulkWrite operation is not currently audited");
        return failedMessageCollection.bulkWrite(requests, options);
    }

    @Override
    public void insertOne(Document document) {
        failedMessageCollection.insertOne(document);
        insertFailedMessageAuditDocument(document);
    }

    @Override
    public void insertOne(Document document, InsertOneOptions options) {
        failedMessageCollection.insertOne(document, options);
        insertFailedMessageAuditDocument(document);
    }

    @Override
    public void insertMany(List<? extends Document> documents) {
        failedMessageCollection.insertMany(documents);
        documents.forEach(this::insertFailedMessageAuditDocument);
    }

    @Override
    public void insertMany(List<? extends Document> documents, InsertManyOptions options) {
        failedMessageCollection.insertMany(documents, options);
        documents.forEach(this::insertFailedMessageAuditDocument);
    }

    private void insertFailedMessageAuditDocument(Document document) {
        failedMessageAuditCollection.insertOne(new Document()
                .append(AUDIT_ACTION_KEY, INSERT_AUDIT_ACTION)
                .append(AUDIT_DATE_TIME_KEY, Instant.now())
                .append(DOCUMENT_KEY, document)
        );
    }

    @Override
    public DeleteResult deleteOne(Bson filter) {
        createFailedMessageDeleteAuditDocument(filter);
        return failedMessageCollection.deleteOne(filter);
    }

    private void createFailedMessageDeleteAuditDocument(Bson filter) {
        Optional.ofNullable(failedMessageCollection.find(filter).first())
                .ifPresent(document -> failedMessageAuditCollection
                        .insertOne(new Document()
                                .append(AUDIT_ACTION_KEY, DELETE_AUDIT_ACTION)
                                .append(AUDIT_DATE_TIME_KEY, Instant.now())
                                .append(DOCUMENT_KEY, document)));
    }

    @Override
    public DeleteResult deleteOne(Bson filter, DeleteOptions options) {
        createFailedMessageDeleteAuditDocument(filter);
        return failedMessageCollection.deleteOne(filter, options);
    }

    @Override
    public DeleteResult deleteMany(Bson filter) {
        failedMessageCollection.find(filter)
                .forEach((Consumer<Document>) document -> failedMessageAuditCollection.insertOne(new Document()
                        .append(AUDIT_ACTION_KEY, DELETE_AUDIT_ACTION)
                        .append(AUDIT_DATE_TIME_KEY, Instant.now())
                        .append(DOCUMENT_KEY, document)));
        return markDocumentsAsDeleted(filter, failedMessageCollection.deleteMany(filter));
    }

    @Override
    public DeleteResult deleteMany(Bson filter, DeleteOptions options) {
        return markDocumentsAsDeleted(filter, failedMessageCollection.deleteMany(filter, options));
    }

    private DeleteResult markDocumentsAsDeleted(Bson filter, DeleteResult deleteResult) {
        if (deleteResult.getDeletedCount() > 0) {
            failedMessageAuditCollection
                    .find(filter)
                    .forEach((Consumer<Document>) document -> failedMessageAuditCollection.insertOne(new Document()
                            .append(AUDIT_ACTION_KEY, DELETE_AUDIT_ACTION)
                            .append(AUDIT_DATE_TIME_KEY, Instant.now())
                            .append(DOCUMENT_KEY, document)));
        }
        return deleteResult;
    }

    @Override
    public UpdateResult replaceOne(Bson filter, Document replacement) {
        return replaceDocument(replacement, failedMessageCollection.replaceOne(filter, replacement));
    }

    @Override
    public UpdateResult replaceOne(Bson filter, Document replacement, UpdateOptions updateOptions) {
        return replaceDocument(replacement, failedMessageCollection.replaceOne(filter, replacement, updateOptions));
    }

    private UpdateResult replaceDocument(Document replacement, UpdateResult updateResult) {
        if (!updateResult.wasAcknowledged() || updateResult.getMatchedCount() > 0) {
            failedMessageAuditCollection.insertOne(new Document()
                    .append(AUDIT_ACTION_KEY, REPLACE_AUDIT_ACTION)
                    .append(AUDIT_DATE_TIME_KEY, Instant.now())
                    .append(DOCUMENT_KEY, replacement));
        }
        return updateResult;
    }

    @Override
    public UpdateResult updateOne(Bson filter, Bson update) {
        return createFailedMessageUpdateAuditDocument(
                filter,
                idFilter -> failedMessageCollection.updateOne(idFilter.map(Bson.class::cast).orElse(filter), update)
        );
    }

    @Override
    public UpdateResult updateOne(Bson filter, Bson update, UpdateOptions updateOptions) {
        return createFailedMessageUpdateAuditDocument(
                filter,
                idFilter -> failedMessageCollection.updateOne(idFilter.map(Bson.class::cast).orElse(filter), update, updateOptions)
        );
    }

    public UpdateResult createFailedMessageUpdateAuditDocument(Bson filter, Function<Optional<Document>, UpdateResult> updateOperation) {
        final Optional<Document> id = Optional.ofNullable(failedMessageCollection
                .find(filter)
                .projection(new Document("_id", 1))
                .first());
        final UpdateResult updateResult = updateOperation.apply(id);
        id.ifPresent(x -> failedMessageAuditCollection.insertOne(new Document()
                .append(AUDIT_ACTION_KEY, UPDATE_AUDIT_ACTION)
                .append(AUDIT_DATE_TIME_KEY, Instant.now())
                .append(DOCUMENT_KEY, failedMessageCollection.find(x).first())
        ));
        return updateResult;
    }

    @Override
    public UpdateResult updateMany(Bson filter, Bson update) {
        LOGGER.warn("updateMany operation is not currently audited");
        return failedMessageCollection.updateMany(filter, update);
    }

    @Override
    public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
        LOGGER.warn("updateMany with UpdateOptions operation is not currently audited");
        return failedMessageCollection.updateMany(filter, update, updateOptions);
    }

    @Override
    public Document findOneAndDelete(Bson filter) {
        LOGGER.warn("findOneAndDelete operation is not currently audited");
        return failedMessageCollection.findOneAndDelete(filter);
    }

    @Override
    public Document findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
        LOGGER.warn("findOneAndDelete operation is not currently audited");
        return failedMessageCollection.findOneAndDelete(filter, options);
    }

    @Override
    public Document findOneAndReplace(Bson filter, Document replacement) {
        LOGGER.warn("findOneAndReplace operation is not currently audited");
        return failedMessageCollection.findOneAndReplace(filter, replacement);
    }

    @Override
    public Document findOneAndReplace(Bson filter, Document replacement, FindOneAndReplaceOptions options) {
        LOGGER.warn("findOneAndReplace operation is not currently audited");
        return failedMessageCollection.findOneAndReplace(filter, replacement, options);
    }

    @Override
    public Document findOneAndUpdate(Bson filter, Bson update) {
        LOGGER.warn("findOneAndUpdate operation is not currently audited");
        return failedMessageCollection.findOneAndUpdate(filter, update);
    }

    @Override
    public Document findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        LOGGER.warn("findOneAndUpdate operation is not currently audited");
        return failedMessageCollection.findOneAndUpdate(filter, update, options);
    }

    @Override
    public void drop() {
        LOGGER.warn("drop operation is not currently audited");
        failedMessageCollection.drop();
    }

    @Override
    public String createIndex(Bson keys) {
        LOGGER.warn("createIndex operation is not currently audited");
        return failedMessageCollection.createIndex(keys);
    }

    @Override
    public String createIndex(Bson keys, IndexOptions indexOptions) {
        LOGGER.warn("createIndex operation is not currently audited");
        return failedMessageCollection.createIndex(keys, indexOptions);
    }

    @Override
    public List<String> createIndexes(List<IndexModel> indexes) {
        LOGGER.warn("createIndex operation is not currently audited");
        return failedMessageCollection.createIndexes(indexes);
    }

    @Override
    public ListIndexesIterable<Document> listIndexes() {
        return failedMessageCollection.listIndexes();
    }

    @Override
    public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> tResultClass) {
        return failedMessageCollection.listIndexes(tResultClass);
    }

    @Override
    public void dropIndex(String indexName) {
        LOGGER.warn("dropIndex(indexName) operation is not currently audited");
        failedMessageCollection.dropIndex(indexName);
    }

    @Override
    public void dropIndex(Bson keys) {
        LOGGER.warn("dropIndex(keys) operation is not currently audited");
        failedMessageCollection.dropIndex(keys);
    }

    @Override
    public void dropIndexes() {
        LOGGER.warn("dropIndexes operation is not currently audited");
        failedMessageCollection.dropIndexes();
    }

    @Override
    public void renameCollection(MongoNamespace newCollectionNamespace) {
        LOGGER.warn("renameCollection operation is not currently audited");
        failedMessageCollection.renameCollection(newCollectionNamespace);
    }

    @Override
    public void renameCollection(MongoNamespace newCollectionNamespace, RenameCollectionOptions renameCollectionOptions) {
        LOGGER.warn("renameCollection operation is not currently audited");
        failedMessageCollection.renameCollection(newCollectionNamespace, renameCollectionOptions);
    }
}
