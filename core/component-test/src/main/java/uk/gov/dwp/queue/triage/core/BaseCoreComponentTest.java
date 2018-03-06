package uk.gov.dwp.queue.triage.core;

import com.mongodb.MongoClient;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoDatabaseCleaner;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoDatabaseLogger;
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
    @Rule
    public final TestRule activeMqBroker = new EmbeddedActiveMQBroker();

    @Rule
    public final TestRule dumpFailedMessagesOnError = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);
            new MongoDatabaseLogger(mongoClient).log("queue-triage", "failedMessage");
        }
    };

    @Before
    public void cleanDatabases() {
        new MongoDatabaseCleaner(mongoClient).cleanCollection("queue-triage", "failedMessage");
        stubMessageClassifierResource.clear();
    }
}
