package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class NamedMessageListenerContainer extends DefaultMessageListenerContainer {

    private final String brokerName;

    public NamedMessageListenerContainer(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerName() {
        return brokerName;
    }
}
