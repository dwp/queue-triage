package uk.gov.dwp.queue.triage.core.stub.app.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
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
import uk.gov.dwp.queue.triage.core.stub.app.jms.StubMessageListener;
import uk.gov.dwp.queue.triage.core.stub.app.repository.MessageClassifierRepository;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

@Configuration
@Import(StubAppRepositoryConfiguration.class)
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
    public DefaultMessageListenerContainer dummyAppMessageListenerContainer(MessageListener stubMessageListener,
                                                                            @Qualifier("activeMqConnectionFactory-" + BROKER_NAME) ConnectionFactory connectionFactory) {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestinationName("some-queue");
        defaultMessageListenerContainer.setMessageListener(stubMessageListener);
        defaultMessageListenerContainer.setTransactionManager(new JmsTransactionManager(connectionFactory));
        defaultMessageListenerContainer.setSessionTransacted(true);
        return defaultMessageListenerContainer;
    }

    @Bean
    public JmsTemplate dummyAppJmsTemplate(@Qualifier("activeMqConnectionFactory-" + BROKER_NAME) ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryPersistent(true);
        return jmsTemplate;
    }

}
