package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;

import java.util.Optional;

import javax.jms.ConnectionFactory;

public class ActiveMQConnectionFactoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQConnectionFactoryFactory.class);
    private final String brokerName;
    private final JmsListenerProperties jmsListenerProperties;

    public ActiveMQConnectionFactoryFactory(String brokerName, JmsListenerProperties jmsListenerProperties) {
        this.brokerName = brokerName;
        this.jmsListenerProperties = jmsListenerProperties;
    }

    public ConnectionFactory create() {
        JmsListenerProperties.BrokerProperties brokerConfig = jmsListenerProperties.getBrokerConfigFor(brokerName)
            .orElseThrow(() -> new RuntimeException("Could not find broker config for broker with name: " + brokerName));
        Optional<JmsListenerProperties.BrokerProperties.TlsProperties> tlsConfig = Optional.ofNullable(brokerConfig.getTls());

        return tlsConfig.map(tls -> {
            try {
                LOGGER.info("Found TLS configuration for broker with name: {}. Creating appropriate ssl connection factory.", brokerName);
                ActiveMQSslConnectionFactory activeMQSslConnectionFactory = new ActiveMQSslConnectionFactory(brokerConfig.getUrl());

                activeMQSslConnectionFactory.setKeyStore(tls.getKeyStoreFilePath());
                activeMQSslConnectionFactory.setKeyStorePassword(String.valueOf(tls.getKeyStorePassword()));
                activeMQSslConnectionFactory.setTrustStore(tls.getTrustStoreFilePath());
                activeMQSslConnectionFactory.setTrustStorePassword(String.valueOf(tls.getTrustStorePassword()));
                return (ConnectionFactory) activeMQSslConnectionFactory;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElseGet(() -> {
            LOGGER.info("Creating standard connection factory for broker with name: {}", brokerName);
            return new ActiveMQConnectionFactory(brokerConfig.getUrl());
        });
    }

}
