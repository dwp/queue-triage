package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.RenameCollectionOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

/**
 * The purpose of this test to record (and document) the fact that certain operations are not (yet) audited.
 */
public class AuditingMongoCollectionDelegationTest {

    @Rule
    public MockitoRule mockitoJUnit = MockitoJUnit.rule();
    @Mock
    private MongoCollection<Document> auditCollection;
    @Mock
    private MongoCollection<Document> sourceCollection;
    @Mock
    private Bson filter;
    @Mock
    private Document document;
    @Mock
    private Bson keys;
    private TestLogger logger = TestLoggerFactory.getTestLogger(AuditingMongoCollection.class);

    private AuditingMongoCollection underTest;

    @Before
    public void setUp() { ;
        underTest = new AuditingMongoCollection(sourceCollection, auditCollection);
    }

    @After
    public void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void getNamespace() {
        underTest.getNamespace();
        verify(sourceCollection).getNamespace();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void getDocumentClass() {
        underTest.getDocumentClass();
        verify(sourceCollection).getDocumentClass();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void getCodecRegistry() {
        underTest.getCodecRegistry();

        verify(sourceCollection).getCodecRegistry();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void getReadPreference() {
        underTest.getReadPreference();

        verify(sourceCollection).getReadPreference();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void getWriteConcern() {
        underTest.getWriteConcern();

        verify(sourceCollection).getWriteConcern();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void getReadConcern() {
        underTest.getReadConcern();

        verify(sourceCollection).getReadConcern();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void withDocumentClass() {
        underTest.withDocumentClass(Number.class);

        verify(sourceCollection).withDocumentClass(Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void withCodecRegistry() {
        final CodecRegistry codecRegistry = mock(CodecRegistry.class);

        underTest.withCodecRegistry(codecRegistry);

        verify(sourceCollection).withCodecRegistry(codecRegistry);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void count() {
        underTest.count();

        verify(sourceCollection).count();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void countWithFilter() {
        underTest.count(document);

        verify(sourceCollection).count(document);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void countWithFilterAndOptions() {
        CountOptions countOptions = mock(CountOptions.class);

        underTest.count(document, countOptions);

        verify(sourceCollection).count(document, countOptions);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void distinct() {
        underTest.distinct("foo", Number.class);

        verify(sourceCollection).distinct("foo", Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void distinctWithFilter() {
        underTest.distinct("foo", filter, Number.class);

        verify(sourceCollection).distinct("foo", filter, Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void find() {
        underTest.find();

        verify(sourceCollection).find();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void findWithResultClass() {
        underTest.find(Number.class);

        verify(sourceCollection).find(Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void findNumberWithFilter() {
        underTest.find(filter);

        verify(sourceCollection).find(filter);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void findWithFilterAndResultClass() {
        underTest.find(filter, Number.class);

        verify(sourceCollection).find(filter, Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void aggregateWithPipeline() {
        underTest.aggregate(Collections.emptyList());

        verify(sourceCollection).aggregate(Collections.emptyList());
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void aggregateWithPipelineAndResultClass() {
        underTest.aggregate(Collections.emptyList(), Number.class);

        verify(sourceCollection).aggregate(Collections.emptyList(), Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void mapReduce() {
        underTest.mapReduce("map", "reduce");

        verify(sourceCollection).mapReduce("map", "reduce");
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void mapReduceResultClass() {
        underTest.mapReduce("map", "reduce", Number.class);

        verify(sourceCollection).mapReduce("map", "reduce", Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void bulkWrite() {
        underTest.bulkWrite(Collections.emptyList());

        verify(sourceCollection).bulkWrite(Collections.emptyList());
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "bulkWrite")));
    }

    @Test
    public void bulkWriteWithOptions() {
        BulkWriteOptions bulkWriteOptions = mock(BulkWriteOptions.class);
        underTest.bulkWrite(Collections.emptyList(), bulkWriteOptions);

        verify(sourceCollection).bulkWrite(Collections.emptyList(), bulkWriteOptions);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "bulkWrite with options")));
    }

    @Test
    public void updateMany() {
        underTest.updateMany(filter, document);

        verify(sourceCollection).updateMany(filter, document);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "updateMany")));
    }

    @Test
    public void findOneAndDeleteWithFilter() {
        underTest.findOneAndDelete(filter);

        verify(sourceCollection).findOneAndDelete(filter);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "findOneAndDelete")));
    }

    @Test
    public void findOneAndDeleteWithFilterAndOptions() {
        final FindOneAndDeleteOptions findOneAndDeleteOptions = mock(FindOneAndDeleteOptions.class);

        underTest.findOneAndDelete(filter, findOneAndDeleteOptions);

        verify(sourceCollection).findOneAndDelete(filter, findOneAndDeleteOptions);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "findOneAndDelete with options")));
    }

    @Test
    public void findOneAndReplaceWithFilter() {
        underTest.findOneAndReplace(filter, document);

        verify(sourceCollection).findOneAndReplace(filter, document);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "findOneAndReplace")));
    }

    @Test
    public void findOneAndReplaceWithFilterAndOptions() {
        final FindOneAndReplaceOptions findOneAndReplaceOptions = mock(FindOneAndReplaceOptions.class);

        underTest.findOneAndReplace(filter, document, findOneAndReplaceOptions);

        verify(sourceCollection).findOneAndReplace(filter, document, findOneAndReplaceOptions);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "findOneAndReplace with options")));
    }

    @Test
    public void drop() {
        underTest.drop();

        verify(sourceCollection).drop();
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "drop")));
    }

    @Test
    public void createIndex() {
        underTest.createIndex(keys);

        verify(sourceCollection).createIndex(keys);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "createIndex")));
    }

    @Test
    public void createIndexWithOptions() {
        final IndexOptions indexOptions = mock(IndexOptions.class);

        underTest.createIndex(keys, indexOptions);

        verify(sourceCollection).createIndex(keys, indexOptions);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "createIndex with options")));
    }

    @Test
    public void createIndexes() {
        underTest.createIndexes(Collections.emptyList());

        verify(sourceCollection).createIndexes(Collections.emptyList());
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "createIndexes")));
    }

    @Test
    public void listIndexes() {
        underTest.listIndexes();

        verify(sourceCollection).listIndexes();
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void listIndexesWithResultClass() {
        underTest.listIndexes(Number.class);

        verify(sourceCollection).listIndexes(Number.class);
        verifyZeroInteractions(auditCollection);
    }

    @Test
    public void dropIndex() {
        underTest.dropIndex("index-name");

        verify(sourceCollection).dropIndex("index-name");
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "dropIndex with name")));
    }

    @Test
    public void dropIndexWithKeys() {
        underTest.dropIndex(keys);

        verify(sourceCollection).dropIndex(keys);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "dropIndex with keys")));
    }

    @Test
    public void dropIndexes() {
        underTest.dropIndexes();

        verify(sourceCollection).dropIndexes();
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "dropIndexes")));
    }

    @Test
    public void renameCollection() {
        final MongoNamespace newCollectionNamespace = mock(MongoNamespace.class);

        underTest.renameCollection(newCollectionNamespace);

        verify(sourceCollection).renameCollection(newCollectionNamespace);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "renameCollection")));
    }

    @Test
    public void renameCollectionWithOptions() {
        final MongoNamespace newCollectionNamespace = mock(MongoNamespace.class);
        final RenameCollectionOptions renameCollectionOptions = mock(RenameCollectionOptions.class);

        underTest.renameCollection(newCollectionNamespace, renameCollectionOptions);

        verify(sourceCollection).renameCollection(newCollectionNamespace, renameCollectionOptions);
        verifyZeroInteractions(auditCollection);
        assertThat(logger.getLoggingEvents(), contains(warn("{} operation is not currently audited", "renameCollection with options")));
    }
}