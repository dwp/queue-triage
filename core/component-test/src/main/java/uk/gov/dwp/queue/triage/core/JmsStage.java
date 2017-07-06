package uk.gov.dwp.queue.triage.core;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

@JGivenStage
public class JmsStage extends Stage<JmsStage> {

    @Autowired
    private JmsTemplate dummyAppJmsTemplate;

    public JmsStage anInvalidMessageIsSentTo$OnBroker$(String destination, String brokerName) {
        dummyAppJmsTemplate.send(destination, session -> session.createTextMessage("Hello"));
        return this;
    }

    public JmsStage anApplicationIsListeningTo$OnBroker$(String destination, String brokerName) {
        return this;
    }
}
