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
import java.util.function.Supplier;

/**
 * Writes an audit document to a separate collection in the format:
 * <pre>
 * {
 *   "_id" : ObjectId("5af56fc30b41674d8030c9b0"),
 *    "auditAction" : "INSERT",
 *    "auditDateTime" : ISODate("2018-05-11T10:26:11.666Z"),
 *    "document" : {
 *      "_id" : "e30eaa4a-4700-4a37-a771-dac30eddbd45",
 *      ...
 *    }
 * }
 * </pre>
 * NOTE: At this time certain operations are NOT audited (as they are not used by the application), if an operation that
 * is not audited is executed a message will be written to the application logs at level WARN
 */
public class AuditingMongoCollection implements MongoCollection<Document> {

    static final String AUDIT_ACTION_KEY = "auditAction";
    static final String AUDIT_DATE_TIME_KEY = "auditDateTime";
    static final String DOCUMENT_KEY = "document";

    static final String DELETE_AUDIT_ACTION = "DELETE";
    static final String INSERT_AUDIT_ACTION = "INSERT";
    static final String REPLACE_AUDIT_ACTION = "REPLACE";
    static final String UPDATE_AUDIT_ACTION = "UPDATE";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditingMongoCollection.class);

    private final MongoCollection<Document> sourceCollection;
    private final MongoCollection<Document> auditCollection;

    public AuditingMongoCollection(MongoCollection<Document> sourceCollection,
                                   MongoCollection<Document> auditCollection) {
        this.sourceCollection = sourceCollection;
        this.auditCollection = auditCollection;
    }

    @Override
    public MongoNamespace getNamespace() {
        return sourceCollection.getNamespace();
    }

    @Override
    public Class<Document> getDocumentClass() {
        return sourceCollection.getDocumentClass();
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return sourceCollection.getCodecRegistry();
    }

    @Override
    public ReadPreference getReadPreference() {
        return sourceCollection.getReadPreference();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return sourceCollection.getWriteConcern();
    }

    @Override
    public ReadConcern getReadConcern() {
        return sourceCollection.getReadConcern();
    }

    @Override
    public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> clazz) {
        return sourceCollection.withDocumentClass(clazz);
    }

    @Override
    public MongoCollection<Document> withCodecRegistry(CodecRegistry codecRegistry) {
        return sourceCollection.withCodecRegistry(codecRegistry);
    }

    @Override
    public MongoCollection<Document> withReadPreference(ReadPreference readPreference) {
        return sourceCollection.withReadPreference(readPreference);
    }

    @Override
    public MongoCollection<Document> withWriteConcern(WriteConcern writeConcern) {
        return sourceCollection.withWriteConcern(writeConcern);
    }

    @Override
    public MongoCollection<Document> withReadConcern(ReadConcern readConcern) {
        return sourceCollection.withReadConcern(readConcern);
    }

    @Override
    public long count() {
        return sourceCollection.count();
    }

    @Override
    public long count(Bson filter) {
        return sourceCollection.count(filter);
    }

    @Override
    public long count(Bson filter, CountOptions options) {
        return sourceCollection.count(filter, options);
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Class<TResult> tResultClass) {
        return sourceCollection.distinct(fieldName, tResultClass);
    }

    @Override
    public <TResult> DistinctIterable<TResult> distinct(String fieldName, Bson filter, Class<TResult> tResultClass) {
        return sourceCollection.distinct(fieldName, filter, tResultClass);
    }

    @Override
    public FindIterable<Document> find() {
        return sourceCollection.find();
    }

    @Override
    public <TResult> FindIterable<TResult> find(Class<TResult> tResultClass) {
        return sourceCollection.find(tResultClass);
    }

    @Override
    public FindIterable<Document> find(Bson filter) {
        return sourceCollection.find(filter);
    }

    @Override
    public <TResult> FindIterable<TResult> find(Bson filter, Class<TResult> tResultClass) {
        return sourceCollection.find(filter, tResultClass);
    }

    @Override
    public AggregateIterable<Document> aggregate(List<? extends Bson> pipeline) {
        return sourceCollection.aggregate(pipeline);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return sourceCollection.aggregate(pipeline, tResultClass);
    }

    @Override
    public MapReduceIterable<Document> mapReduce(String mapFunction, String reduceFunction) {
        return sourceCollection.mapReduce(mapFunction, reduceFunction);
    }

    @Override
    public <TResult> MapReduceIterable<TResult> mapReduce(String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
        return sourceCollection.mapReduce(mapFunction, reduceFunction, tResultClass);
    }

    @Override
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends Document>> requests) {
        warnOperationNotAudited("bulkWrite");
        return sourceCollection.bulkWrite(requests);
    }

    @Override
    public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends Document>> requests, BulkWriteOptions options) {
        warnOperationNotAudited("bulkWrite with options");
        return sourceCollection.bulkWrite(requests, options);
    }

    @Override
    public void insertOne(Document document) {
        sourceCollection.insertOne(document);
        createInsertAuditDocument(document);
    }

    @Override
    public void insertOne(Document document, InsertOneOptions options) {
        sourceCollection.insertOne(document, options);
        createInsertAuditDocument(document);
    }

    @Override
    public void insertMany(List<? extends Document> documents) {
        sourceCollection.insertMany(documents);
        documents.forEach(this::createInsertAuditDocument);
    }

    @Override
    public void insertMany(List<? extends Document> documents, InsertManyOptions options) {
        sourceCollection.insertMany(documents, options);
        documents.forEach(this::createInsertAuditDocument);
    }

    @Override
    public DeleteResult deleteOne(Bson filter) {
        return auditDeleteOne(filter, () -> sourceCollection.deleteOne(filter));
    }

    @Override
    public DeleteResult deleteOne(Bson filter, DeleteOptions options) {
        return auditDeleteOne(filter, () -> sourceCollection.deleteOne(filter, options));
    }

    @Override
    public DeleteResult deleteMany(Bson filter) {
        return auditDeleteMany(filter, () -> sourceCollection.deleteMany(filter));
    }

    @Override
    public DeleteResult deleteMany(Bson filter, DeleteOptions options) {
        return auditDeleteMany(filter, () -> sourceCollection.deleteMany(filter, options));
    }

    private DeleteResult auditDeleteOne(Bson filter, Supplier<DeleteResult> deleteOneCommand) {
        Optional.ofNullable(sourceCollection.find(filter).first())
                .ifPresent(this::createDeleteAuditDocument);
        return deleteOneCommand.get();
    }

    private DeleteResult auditDeleteMany(Bson filter, Supplier<DeleteResult> deleteManyCommand) {
        sourceCollection
                .find(filter)
                .forEach((Consumer<Document>) this::createDeleteAuditDocument);
        return deleteManyCommand.get();
    }

    private void createInsertAuditDocument(Document document) {
        auditCollection.insertOne(new Document()
                .append(AUDIT_ACTION_KEY, INSERT_AUDIT_ACTION)
                .append(AUDIT_DATE_TIME_KEY, Instant.now())
                .append(DOCUMENT_KEY, document)
        );
    }

    private void createDeleteAuditDocument(Document document) {
        auditCollection.insertOne(new Document()
                .append(AUDIT_ACTION_KEY, DELETE_AUDIT_ACTION)
                .append(AUDIT_DATE_TIME_KEY, Instant.now())
                .append(DOCUMENT_KEY, document));
    }

    @Override
    public UpdateResult replaceOne(Bson filter, Document replacement) {
        return createReplaceAuditDocument(replacement, sourceCollection.replaceOne(filter, replacement));
    }

    @Override
    public UpdateResult replaceOne(Bson filter, Document replacement, UpdateOptions updateOptions) {
        return createReplaceAuditDocument(replacement, sourceCollection.replaceOne(filter, replacement, updateOptions));
    }

    private UpdateResult createReplaceAuditDocument(Document replacement, UpdateResult updateResult) {
        if (!updateResult.wasAcknowledged() || updateResult.getMatchedCount() > 0) {
            auditCollection.insertOne(new Document()
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
                idFilter -> sourceCollection.updateOne(idFilter.map(Bson.class::cast).orElse(filter), update)
        );
    }

    @Override
    public UpdateResult updateOne(Bson filter, Bson update, UpdateOptions updateOptions) {
        return createFailedMessageUpdateAuditDocument(
                filter,
                idFilter -> sourceCollection.updateOne(idFilter.map(Bson.class::cast).orElse(filter), update, updateOptions)
        );
    }

    private UpdateResult createFailedMessageUpdateAuditDocument(Bson filter, Function<Optional<Document>, UpdateResult> updateOperation) {
        final Optional<Document> id = Optional.ofNullable(sourceCollection
                .find(filter)
                .projection(new Document("_id", 1))
                .first());
        final UpdateResult updateResult = updateOperation.apply(id);
        id.ifPresent(x -> auditCollection.insertOne(new Document()
                .append(AUDIT_ACTION_KEY, UPDATE_AUDIT_ACTION)
                .append(AUDIT_DATE_TIME_KEY, Instant.now())
                .append(DOCUMENT_KEY, sourceCollection.find(x).first())
        ));
        return updateResult;
    }

    @Override
    public UpdateResult updateMany(Bson filter, Bson update) {
        warnOperationNotAudited("updateMany");
        return sourceCollection.updateMany(filter, update);
    }

    @Override
    public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
        warnOperationNotAudited("updateMany with options");
        return sourceCollection.updateMany(filter, update, updateOptions);
    }

    @Override
    public Document findOneAndDelete(Bson filter) {
        warnOperationNotAudited("findOneAndDelete");
        return sourceCollection.findOneAndDelete(filter);
    }

    @Override
    public Document findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
        warnOperationNotAudited("findOneAndDelete with options");
        return sourceCollection.findOneAndDelete(filter, options);
    }

    @Override
    public Document findOneAndReplace(Bson filter, Document replacement) {
        warnOperationNotAudited("findOneAndReplace");
        return sourceCollection.findOneAndReplace(filter, replacement);
    }

    @Override
    public Document findOneAndReplace(Bson filter, Document replacement, FindOneAndReplaceOptions options) {
        warnOperationNotAudited("findOneAndReplace with options");
        return sourceCollection.findOneAndReplace(filter, replacement, options);
    }

    @Override
    public Document findOneAndUpdate(Bson filter, Bson update) {
        warnOperationNotAudited("findOneAndUpdate");
        return sourceCollection.findOneAndUpdate(filter, update);
    }

    @Override
    public Document findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        warnOperationNotAudited("findOneAndUpdate with options");
        return sourceCollection.findOneAndUpdate(filter, update, options);
    }

    @Override
    public void drop() {
        warnOperationNotAudited("drop");
        sourceCollection.drop();
    }

    @Override
    public String createIndex(Bson keys) {
        warnOperationNotAudited("createIndex");
        return sourceCollection.createIndex(keys);
    }

    @Override
    public String createIndex(Bson keys, IndexOptions indexOptions) {
        warnOperationNotAudited("createIndex with options");
        return sourceCollection.createIndex(keys, indexOptions);
    }

    @Override
    public List<String> createIndexes(List<IndexModel> indexes) {
        warnOperationNotAudited("createIndexes");
        return sourceCollection.createIndexes(indexes);
    }

    @Override
    public ListIndexesIterable<Document> listIndexes() {
        return sourceCollection.listIndexes();
    }

    @Override
    public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> tResultClass) {
        return sourceCollection.listIndexes(tResultClass);
    }

    @Override
    public void dropIndex(String indexName) {
        warnOperationNotAudited("dropIndex with name");
        sourceCollection.dropIndex(indexName);
    }

    @Override
    public void dropIndex(Bson keys) {
        warnOperationNotAudited("dropIndex with keys");
        sourceCollection.dropIndex(keys);
    }

    @Override
    public void dropIndexes() {
        warnOperationNotAudited("dropIndexes");
        sourceCollection.dropIndexes();
    }

    @Override
    public void renameCollection(MongoNamespace newCollectionNamespace) {
        warnOperationNotAudited("renameCollection");
        sourceCollection.renameCollection(newCollectionNamespace);
    }

    @Override
    public void renameCollection(MongoNamespace newCollectionNamespace, RenameCollectionOptions renameCollectionOptions) {
        warnOperationNotAudited("renameCollection with options");
        sourceCollection.renameCollection(newCollectionNamespace, renameCollectionOptions);
    }

    protected void warnOperationNotAudited(String operation) {
        LOGGER.warn("{} operation is not currently audited", operation);
    }
}
