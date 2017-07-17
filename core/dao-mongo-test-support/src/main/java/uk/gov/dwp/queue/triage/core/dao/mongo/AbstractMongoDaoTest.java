package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
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
    protected DBCollection collection;

    @Before
    public void setUp() {
        collection = mongoClient.getDB(mongoDaoProperties.getDbName()).getCollection(getCollectionName());
        collection.remove(new BasicDBObject());
    }

    @After
    public void tearDown() {
        collection.remove(new BasicDBObject());
    }

    protected abstract String getCollectionName();

}
