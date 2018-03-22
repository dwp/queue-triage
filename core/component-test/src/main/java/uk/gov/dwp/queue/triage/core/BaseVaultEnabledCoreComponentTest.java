package uk.gov.dwp.queue.triage.core;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mongodb.MongoClient;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(value = "vault-enabled-component-test")
public class BaseVaultEnabledCoreComponentTest<STAGE> extends SimpleSpringRuleScenarioTest<STAGE> {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private StubMessageClassifierResource stubMessageClassifierResource;
    @Rule
    public final TestRule activeMqBroker = new EmbeddedActiveMQBroker();

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(1234);

    @Rule
    public final TestRule dumpFailedMessagesOnError = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);
            new MongoDatabaseLogger(mongoClient).log("queue-triage", "failedMessage");
        }
    };

    @BeforeClass
    public static void startMockVaultService() {
        wireMockRule.addStubMapping(stubFor(
            get(urlPathEqualTo("/vault/v1/queue-triage/mongo-dao/failedMessageUser"))
                .withHeader("X-Vault-Token", equalTo("VAULT-TEST-TOKEN"))

                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("{\"data\": {\"value\": \"Passw0rd\"}}")
                        .withHeader("Content-Type", "text/html")
                )));
    }

    @Before
    public void cleanDatabases() {
        new MongoDatabaseCleaner(mongoClient).cleanCollection("queue-triage", "failedMessage");
        stubMessageClassifierResource.clear();
    }
}
