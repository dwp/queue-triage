package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoConfig;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        MongoDaoProperties.class,
        MongoDaoConfig.class,
})
@TestPropertySource("/mongo-dao-test.properties")
public abstract class AbstractMongoDaoTest {

    @Autowired
    protected MongoClient mongoClient;
    @Autowired
    protected MongoDaoProperties mongoDaoProperties;
    protected MongoCollection<Document> collection;

    @Before
    public void setUp() {
        collection = mongoClient.getDatabase(mongoDaoProperties.getDbName()).getCollection(getCollectionName());
        collection.deleteMany(new Document());
    }

    @After
    public void tearDown() {
        collection.deleteMany(new Document());
    }

    protected abstract String getCollectionName();

}
