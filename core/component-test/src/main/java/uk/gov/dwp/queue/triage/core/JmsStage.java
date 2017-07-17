package uk.gov.dwp.queue.triage.core;

import com.mongodb.MongoClient;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoDatabaseCleaner;

@JGivenStage
public class JmsStage extends Stage<JmsStage> {

    @Autowired
    private JmsTemplate dummyAppJmsTemplate;
    @Autowired
    private MongoClient mongoClient;

    @BeforeScenario
    public void cleanDatabases() {
        new MongoDatabaseCleaner(mongoClient).cleanDatabase("queue-triage");
    }

    public JmsStage anInvalidMessageIsSentTo$OnBroker$(String destination, String brokerName) {
        dummyAppJmsTemplate.send(destination, session -> session.createTextMessage("Hello"));
        return this;
    }

    public JmsStage anApplicationIsListeningTo$OnBroker$(String destination, String brokerName) {
        return this;
    }
}
