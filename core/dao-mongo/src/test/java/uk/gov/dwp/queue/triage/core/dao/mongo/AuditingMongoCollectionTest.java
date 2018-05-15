package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import org.bson.Document;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static uk.gov.dwp.queue.triage.core.dao.mongo.AuditingMongoCollection.AUDIT_ACTION_KEY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.AuditingMongoCollection.AUDIT_DATE_TIME_KEY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.AuditingMongoCollection.DELETE_AUDIT_ACTION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.AuditingMongoCollection.DOCUMENT_KEY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.AuditingMongoCollection.INSERT_AUDIT_ACTION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.AuditingMongoCollection.UPDATE_AUDIT_ACTION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DocumentMatcher.hasField;

public class AuditingMongoCollectionTest extends AbstractMongoDaoTest {

    private final String failedMessageId = FailedMessageId.newFailedMessageId().getId().toString();

    private MongoCollection<Document> failedMessageAuditCollection;
    private AuditingMongoCollection underTest;

    @Before
    public void setUp() {
        super.setUp();
        failedMessageAuditCollection = mongoClient
                .getDatabase(mongoDaoProperties.getDbName())
                .getCollection("failedMessageAudit");
        underTest = new AuditingMongoCollection(collection, failedMessageAuditCollection);
    }

    @Test
    public void insertOneAudited() {
        underTest.insertOne(documentWithId(failedMessageId).append("foo", "bar"));

        assertThat(collection.find(documentWithId(failedMessageId)).first(), validFailedMessageDocument(hasField("foo", equalTo("bar"))));
        assertThat(auditDocumentFor(failedMessageId), validFailedMessageAuditDocument(INSERT_AUDIT_ACTION, hasField("foo", equalTo("bar")))
        );
    }

    @Test
    public void insertOneWithOptionsIsAudited() {
        underTest.insertOne(documentWithId(failedMessageId).append("foo", "bar"), new InsertOneOptions());

        assertThat(collection.find(documentWithId(failedMessageId)).first(), validFailedMessageDocument(hasField("foo", equalTo("bar"))));
        assertThat(auditDocumentFor(failedMessageId), validFailedMessageAuditDocument("INSERT", hasField("foo", equalTo("bar"))));
    }

    @Test
    public void insertManyIsAudited() {
        underTest.insertMany(Collections.singletonList(documentWithId(failedMessageId).append("foo", "bar")));

        assertThat(collection.find(documentWithId(failedMessageId)).first(), validFailedMessageDocument(hasField("foo", equalTo("bar"))));
        assertThat(auditDocumentFor(failedMessageId), validFailedMessageAuditDocument("INSERT", hasField("foo", equalTo("bar"))));
    }

    @Test
    public void insertManyWithOptionsIsAudited() {
        underTest.insertMany(Collections.singletonList(documentWithId(failedMessageId).append("foo", "bar")), new InsertManyOptions());

        assertThat(collection.find(documentWithId(failedMessageId)).first(), validFailedMessageDocument(hasField("foo", equalTo("bar"))));
        assertThat(auditDocumentFor(failedMessageId), validFailedMessageAuditDocument("INSERT", hasField("foo", equalTo("bar"))));
    }

    @Test
    public void deleteOneIsAudited() {
        collection.insertOne(documentWithId(failedMessageId).append("foo", "bar"));

        underTest.deleteOne(documentWithId(failedMessageId));

        assertThat(collection.find(documentWithId(failedMessageId)).first(), is(nullValue()));
        assertThat(auditDocumentFor(failedMessageId), validDeleteFailedMessageAuditDocument());
    }

    @Test
    public void deleteOneWithOptionsIsAudited() {
        collection.insertOne(documentWithId(failedMessageId).append("foo", "bar"));

        underTest.deleteOne(documentWithId(failedMessageId), new DeleteOptions());

        assertThat(collection.find(documentWithId(failedMessageId)).first(), is(nullValue()));
        assertThat(auditDocumentFor(failedMessageId), validDeleteFailedMessageAuditDocument());
    }

    @Test
    public void deleteManyIsAudited() {
        collection.insertOne(documentWithId(failedMessageId).append("foo", "bar"));

        underTest.deleteMany(documentWithId(failedMessageId));

        assertThat(collection.find(documentWithId(failedMessageId)).first(), is(nullValue()));
        assertThat(auditDocumentFor(failedMessageId), validDeleteFailedMessageAuditDocument());
    }

    @Test
    public void deleteManyWithOptionsIsAudited() {
        collection.insertOne(documentWithId(failedMessageId).append("foo", "bar"));

        underTest.deleteMany(documentWithId(failedMessageId), new DeleteOptions());

        assertThat(collection.find(documentWithId(failedMessageId)).first(), is(nullValue()));
        assertThat(auditDocumentFor(failedMessageId), validDeleteFailedMessageAuditDocument());
    }

    @Test
    public void updateOneIsAudited() {
        collection.insertOne(documentWithId(failedMessageId).append("foo", "bar"));

        underTest.updateOne(documentWithId(failedMessageId), new Document("$set", new Document("foo", "zog")));

        assertThat(collection.find(documentWithId(failedMessageId)).first(), validFailedMessageDocument(hasField("foo", equalTo("zog"))));
        assertThat(auditDocumentFor(failedMessageId), validFailedMessageAuditDocument(UPDATE_AUDIT_ACTION, hasField("foo", equalTo("zog"))));
    }

    @Test
    public void updateOneWithOptionsIsAudited() {
        collection.insertOne(documentWithId(failedMessageId).append("foo", "bar"));

        underTest.updateOne(documentWithId(failedMessageId), new Document("$set", new Document("foo", "zog")));

        assertThat(collection.find(documentWithId(failedMessageId)).first(), validFailedMessageDocument(hasField("foo", equalTo("zog"))));
        assertThat(auditDocumentFor(failedMessageId), validFailedMessageAuditDocument(UPDATE_AUDIT_ACTION, hasField("foo", equalTo("zog"))));
    }

    private Matcher<Document> validFailedMessageAuditDocument(String auditAction, DocumentMatcher documentMatcher) {
        return allOf(
                hasField("_id", not(equalTo(failedMessageId))),
                hasField(DOCUMENT_KEY, allOf(
                        hasField("_id", equalTo(failedMessageId)),
                        documentMatcher)),
                hasField(AUDIT_ACTION_KEY, equalTo(auditAction)),
                hasField(AUDIT_DATE_TIME_KEY, notNullValue())
        );
    }

    private Matcher<Document> validDeleteFailedMessageAuditDocument() {
        return allOf(
                hasField("_id", not(equalTo(failedMessageId))),
                hasField(DOCUMENT_KEY, allOf(
                        hasField("_id", equalTo(failedMessageId)),
                        hasField("foo", equalTo("bar")))),
                hasField(AUDIT_ACTION_KEY, equalTo(DELETE_AUDIT_ACTION)),
                hasField(AUDIT_DATE_TIME_KEY, notNullValue())
        );
    }

    private Matcher<Document> validFailedMessageDocument(DocumentMatcher documentMatcher) {
        return allOf(
                hasField("_id", equalTo(failedMessageId)),
                documentMatcher
        );
    }

    private Document auditDocumentFor(String failedMessageId) {
        return failedMessageAuditCollection.find(new Document(DOCUMENT_KEY + "._id", failedMessageId)).first();
    }

    private Document documentWithId(String failedMessageId) {
        return new Document("_id", failedMessageId);
    }

    @Override
    protected String getCollectionName() {
        return mongoDaoProperties.getFailedMessage().getName();
    }
}