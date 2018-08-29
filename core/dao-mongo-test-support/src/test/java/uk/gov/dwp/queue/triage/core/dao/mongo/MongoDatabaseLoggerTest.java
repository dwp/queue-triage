package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoConfig;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.iterableWithSize;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        MongoDaoProperties.class,
        MongoDaoConfig.class,
})
@TestPropertySource("/test.properties")
public class MongoDatabaseLoggerTest {

    private static final String DB_NAME = "test-db";
    private static final String COLLECTION = "someCollection";
    private static final String ANOTHER_COLLECTION = "anotherCollection";

    @Rule
    public final TestLoggerFactoryResetRule testLoggerFactoryResetRule = new TestLoggerFactoryResetRule();
    @Autowired
    private MongoClient mongoClient;

    private final TestLogger logger = TestLoggerFactory.getTestLogger(MongoDatabaseLogger.class);
    private final TestLogger collectionLogger = TestLoggerFactory.getTestLogger(DB_NAME + "." + COLLECTION);
    private final TestLogger anotherCollectionLogger = TestLoggerFactory.getTestLogger(DB_NAME + "." + ANOTHER_COLLECTION);

    private MongoDatabaseLogger underTest;

    @Before
    public void setUp() {
        underTest = new MongoDatabaseLogger(mongoClient);
        mongoClient.getDatabase(DB_NAME).drop();
    }

    @Test
    public void logDataForAnEmptyCollection() {
        underTest.log(DB_NAME, COLLECTION);

        assertThat(logger.getLoggingEvents(), contains(
                info("Logging documents in {}.{} started", DB_NAME, COLLECTION),
                info("Logging documents in {}.{} complete", DB_NAME, COLLECTION)
        ));
        assertThat(collectionLogger.getLoggingEvents(), iterableWithSize(0));
    }

    @Test
    public void logDataForASingleCollection() {
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).insertOne(new Document("_id", 1).append("foo", "bar"));
        mongoClient.getDatabase(DB_NAME).getCollection(ANOTHER_COLLECTION).insertOne(new Document("_id", 2).append("ham", "eggs"));

        underTest.log(DB_NAME, COLLECTION);

        assertThat(logger.getLoggingEvents(), contains(
                info("Logging documents in {}.{} started", DB_NAME, COLLECTION),
                info("Logging documents in {}.{} complete", DB_NAME, COLLECTION)
        ));
        assertThat(collectionLogger.getLoggingEvents(), contains(
                info("{ \"_id\" : 1, \"foo\" : \"bar\" }")
        ));
        assertThat(anotherCollectionLogger.getLoggingEvents(), emptyIterable());
    }

    @Test
    public void logAllDataInDatabase() {
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).insertOne(new Document("_id", 1).append("foo", "bar"));
        mongoClient.getDatabase(DB_NAME).getCollection(ANOTHER_COLLECTION).insertOne(new Document("_id", 2).append("ham", "eggs"));

        underTest.log(DB_NAME);

        assertThat(logger.getLoggingEvents(), contains(
                info("Logging documents in {} started", DB_NAME),
                info("Logging documents in {} complete", DB_NAME)
        ));
        assertThat(collectionLogger.getLoggingEvents(), contains(
                info("{ \"_id\" : 1, \"foo\" : \"bar\" }")
        ));
        assertThat(anotherCollectionLogger.getLoggingEvents(), contains(
                info("{ \"_id\" : 2, \"ham\" : \"eggs\" }")
        ));
    }
}