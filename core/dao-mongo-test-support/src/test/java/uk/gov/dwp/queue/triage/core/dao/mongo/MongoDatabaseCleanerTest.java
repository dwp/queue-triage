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
import static org.hamcrest.Matchers.is;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        MongoDaoProperties.class,
        MongoDaoConfig.class,
})
@TestPropertySource("/test.properties")
public class MongoDatabaseCleanerTest {

    private static final String DB_NAME = "test-db";
    private static final String COLLECTION = "someCollection";
    private static final String ANOTHER_COLLECTION = "anotherCollection";

    @Rule
    public final TestLoggerFactoryResetRule testLoggerFactoryResetRule = new TestLoggerFactoryResetRule();
    @Autowired
    private MongoClient mongoClient;

    private final TestLogger logger = TestLoggerFactory.getTestLogger(MongoDatabaseCleaner.class);

    private MongoDatabaseCleaner underTest;

    @Before
    public void setUp() {
        underTest = new MongoDatabaseCleaner(mongoClient);
        mongoClient.getDatabase(DB_NAME).drop();
    }

    @Test
    public void logDataForAnEmptyCollection() {
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).insertOne(new Document("_id", 1).append("foo", "bar"));
        mongoClient.getDatabase(DB_NAME).getCollection(ANOTHER_COLLECTION).insertOne(new Document("_id", 2).append("ham", "eggs"));

        underTest.cleanDatabase(DB_NAME);

        assertThat(mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).count(), is(0L));
        assertThat(mongoClient.getDatabase(DB_NAME).getCollection(ANOTHER_COLLECTION).count(), is(0L));
        assertThat(logger.getLoggingEvents(), contains(
                info("Removing data from '{}' database", DB_NAME),
                debug("Removing data from the '{}.{}' collection", DB_NAME, COLLECTION),
                debug("Removing data from the '{}.{}' collection", DB_NAME, ANOTHER_COLLECTION),
                info("Removing data from '{}' database complete", DB_NAME)
        ));
    }


}