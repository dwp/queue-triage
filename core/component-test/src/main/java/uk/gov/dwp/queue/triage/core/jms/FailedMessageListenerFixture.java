package uk.gov.dwp.queue.triage.core.jms;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public class FailedMessageListenerFixture {

    private final TestRestTemplate testRestTemplate;

    public FailedMessageListenerFixture(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    public ResponseEntity<String> stopListenerForBroker(String brokerName) {
        return testRestTemplate.postForEntity(
                "/core/admin/jms-listener/stop/{brokerName}",
                HttpEntity.EMPTY,
                String.class,
                brokerName
        );
    }

    public ResponseEntity<String> startListenerForBroker(String brokerName) {
        return testRestTemplate.postForEntity(
                "/core/admin/jms-listener/start/{brokerName}",
                HttpEntity.EMPTY,
                String.class,
                brokerName
        );
    }

    public ResponseEntity<Boolean> statusOfListenerForBroker(String brokerName) {
        return testRestTemplate.getForEntity(
                "/core/admin/jms-listener/running/{brokerName}",
                Boolean.class,
                brokerName);
    }

    public void readMessagesForBroker(String brokerName) {
        testRestTemplate.put(
                "/core/admin/jms-listener/read-messages/{brokerName}",
                null,
                brokerName
        );
    }

}
