package uk.gov.dwp.queue.triage.core;

import com.mongodb.MongoClient;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoDatabaseCleaner;

@JGivenStage
public class JmsStage extends Stage<JmsStage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsStage.class);

    @Autowired
    private JmsTemplate dummyAppJmsTemplate;
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private DefaultMessageListenerContainer dummyAppMessageListenerContainer;

    @BeforeStage
    public void cleanDatabases() {
        new MongoDatabaseCleaner(mongoClient).cleanAllDatabases();
    }

    @AfterScenario
    public void shutdownDummyApplication() {
        LOGGER.info("Shutting down \"Appliction\" messageListener");
        dummyAppMessageListenerContainer.shutdown();
    }


    public JmsStage anInvalidMessageIsSentTo$OnBroker$(String destination, String brokerName) {
        dummyAppJmsTemplate.send(destination, session -> session.createTextMessage("Hello"));
        return this;
    }

    public JmsStage anApplicationIsListeningTo$OnBroker$(String destination, String brokerName) {
        return this;
    }
}
