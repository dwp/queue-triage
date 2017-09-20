package uk.gov.dwp.queue.triage.core;

import com.mongodb.MongoClient;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoDatabaseCleaner;
import uk.gov.dwp.queue.triage.core.stub.app.resource.StubMessageClassifierResource;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(value = "component-test")
public class BaseCoreComponentTest<STAGE> extends SimpleSpringRuleScenarioTest<STAGE> {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private StubMessageClassifierResource stubMessageClassifierResource;

    @Before
    public void cleanDatabases() {
        new MongoDatabaseCleaner(mongoClient).cleanDatabase("queue-triage");
        stubMessageClassifierResource.clear();
    }
}
