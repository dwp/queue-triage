package uk.gov.dwp.queue.triage.core.stub.app.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.jms.JmsMessagePropertyExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessageTextExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQDestinationExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQFailedMessageFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;
import uk.gov.dwp.queue.triage.core.stub.app.jms.StubMessageListener;
import uk.gov.dwp.queue.triage.core.stub.app.repository.MessageClassifierRepository;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

@Configuration
@Import(StubAppRepositoryConfiguration.class)
@EnableConfigurationProperties
public class StubAppJmsConfiguration {

    public static final String BROKER_NAME = "internal-broker";

    @Bean
    public MessageListener stubMessageListener(MessageClassifierRepository stubMessageClassifierRepository) {
        return new StubMessageListener(
                BROKER_NAME,
                activeMqFailedMessageFactory(),
                stubMessageClassifierRepository,
                defaultMessageClassifier()
        );
    }

    private ActiveMQFailedMessageFactory activeMqFailedMessageFactory() {
        return new ActiveMQFailedMessageFactory(
                new MessageTextExtractor(),
                new ActiveMQDestinationExtractor(BROKER_NAME),
                new JmsMessagePropertyExtractor()
        );
    }

    private MessageClassifier defaultMessageClassifier() {
        return new MessageClassifier(failedMessage -> true, failedMessage -> { throw new RuntimeException("Head Shot!"); });
    }

    @Bean(destroyMethod = "shutdown")
    public DefaultMessageListenerContainer dummyAppMessageListenerContainer(ConnectionFactory dummyAppConnectionFactory,
                                                                            MessageListener stubMessageListener) {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(dummyAppConnectionFactory);
        defaultMessageListenerContainer.setDestinationName("some-queue");
        defaultMessageListenerContainer.setMessageListener(stubMessageListener);
        defaultMessageListenerContainer.setTransactionManager(new JmsTransactionManager(dummyAppConnectionFactory));
        defaultMessageListenerContainer.setSessionTransacted(true);
        return defaultMessageListenerContainer;
    }

    @Bean
    public JmsTemplate dummyAppJmsTemplate(ConnectionFactory dummyAppConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(dummyAppConnectionFactory);
        jmsTemplate.setDeliveryPersistent(true);
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory dummyAppConnectionFactory(JmsListenerProperties jmsListenerProperties) {
        JmsListenerProperties.BrokerProperties brokerProperties = jmsListenerProperties
                .getBrokers()
                .stream()
                .filter(broker -> BROKER_NAME.equalsIgnoreCase(broker.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find configuration for " + BROKER_NAME));
        String brokerUrl = brokerProperties.getUrl();
        brokerUrl += (brokerUrl.contains("?") ? "&" : "?");
        brokerUrl += "jms.redeliveryPolicy.maximumRedeliveries=0";
        return new ActiveMQConnectionFactory(brokerUrl);
    }

}
