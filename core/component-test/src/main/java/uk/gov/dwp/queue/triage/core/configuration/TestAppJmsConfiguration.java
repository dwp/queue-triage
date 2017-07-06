package uk.gov.dwp.queue.triage.core.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

@Configuration
public class TestAppJmsConfiguration {

    @Bean
    public DefaultMessageListenerContainer dummyAppMessageListenerContainer(JmsListenerProperties jmsListenerProperties) {
        ConnectionFactory connectionFactory = createConnectionFactory(jmsListenerProperties, "internal-broker");
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestinationName("some-queue");
        defaultMessageListenerContainer.setMessageListener((MessageListener) message -> {
            if (!(message instanceof BytesMessage)) {
                throw new UnsupportedOperationException("Expected BytesMessage, received: " + message.getClass().getSimpleName());
            }
        });
        defaultMessageListenerContainer.setTransactionManager(new JmsTransactionManager(connectionFactory));
        defaultMessageListenerContainer.setSessionTransacted(true);
        return defaultMessageListenerContainer;
    }

    @Bean
    public JmsTemplate dummyAppJmsTemplate(JmsListenerProperties jmsListenerProperties) {
        JmsTemplate jmsTemplate = new JmsTemplate(createConnectionFactory(jmsListenerProperties, "internal-broker"));
        jmsTemplate.setDeliveryPersistent(true);
        return jmsTemplate;
    }

    private ConnectionFactory createConnectionFactory(JmsListenerProperties jmsListenerProperties, String brokerName) {
        JmsListenerProperties.BrokerProperties brokerProperties = jmsListenerProperties
                .getBrokers()
                .stream()
                .filter(broker -> brokerName.equals(broker.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find configuration for " + brokerName));
        return new ActiveMQConnectionFactory(brokerProperties.getUrl() + "?jms.redeliveryPolicy.maximumRedeliveries=0");
    }

}
