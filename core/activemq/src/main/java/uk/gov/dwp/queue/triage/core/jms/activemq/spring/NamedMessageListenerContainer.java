package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageListenerManager;

public class NamedMessageListenerContainer extends DefaultMessageListenerContainer implements MessageListenerManager {

    private final String brokerName;

    public NamedMessageListenerContainer(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerName() {
        return brokerName;
    }
}
