package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.junit.Test;

import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;

import java.util.Collections;

import javax.jms.ConnectionFactory;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ActiveMQConnectionFactoryFactoryTest {

    private static final String BROKER_NAME = "some-cool-broker";

    @Test
    public void ifNoTlsConffigExistsThenCreateVanillaActiveMQConnectionFactory() throws Exception {
        JmsListenerProperties.BrokerProperties brokerConfig = new JmsListenerProperties.BrokerProperties();
        brokerConfig.setName(BROKER_NAME);
        brokerConfig.setUrl("tcp://name-of-broker:61616");

        JmsListenerProperties testConfig = new JmsListenerProperties();
        testConfig.setBrokers(Collections.singletonList(brokerConfig));

        ActiveMQConnectionFactoryFactory underTest = new ActiveMQConnectionFactoryFactory(BROKER_NAME, testConfig);

        ConnectionFactory connectionFactory = underTest.create();
        assertThat(connectionFactory, instanceOf(ActiveMQConnectionFactory.class));
    }

    @Test
    public void ifTlsOptionsAreDefinedInConfigThenSslConnectionFactoryShouldBeCreated() throws Exception {

        JmsListenerProperties.BrokerProperties.TlsProperties tlsProperties = new JmsListenerProperties.BrokerProperties.TlsProperties();
        String expectedKeystoreFilePath = "keystoreFilePath";
        String expectedKeyStorePassword = "keystore-password";
        String expectedTrustStoreFilePath = "trustStoreFilePath";
        String expectedTrustStorePassword = "trustStorePassword";

        tlsProperties.setKeyStoreFilePath(expectedKeystoreFilePath);
        tlsProperties.setKeyStorePassword(expectedKeyStorePassword.toCharArray());
        tlsProperties.setTrustStoreFilePath(expectedTrustStoreFilePath);
        tlsProperties.setTrustStorePassword(expectedTrustStorePassword.toCharArray());

        JmsListenerProperties.BrokerProperties brokerConfig = new JmsListenerProperties.BrokerProperties();
        brokerConfig.setName(BROKER_NAME);
        brokerConfig.setUrl("tcp://name-of-broker:61616");
        brokerConfig.setTls(tlsProperties);

        JmsListenerProperties testConfig = new JmsListenerProperties();
        testConfig.setBrokers(Collections.singletonList(brokerConfig));

        ActiveMQConnectionFactoryFactory underTest = new ActiveMQConnectionFactoryFactory(BROKER_NAME, testConfig);

        ConnectionFactory connectionFactory = underTest.create();
        assertThat(connectionFactory, instanceOf(ActiveMQSslConnectionFactory.class));

        ActiveMQSslConnectionFactory sslConnectionFactory = (ActiveMQSslConnectionFactory) connectionFactory;

        assertThat(sslConnectionFactory.getKeyStore(), is(expectedKeystoreFilePath));
        assertThat(sslConnectionFactory.getKeyStorePassword(), is(expectedKeyStorePassword));
        assertThat(sslConnectionFactory.getTrustStore(), is(expectedTrustStoreFilePath));
        assertThat(sslConnectionFactory.getTrustStorePassword(), is(expectedTrustStorePassword));
    }
}