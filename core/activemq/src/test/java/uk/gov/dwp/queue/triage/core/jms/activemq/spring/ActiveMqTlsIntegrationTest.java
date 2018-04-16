package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.junit.TlsEmbeddedActiveMQBroker;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerConfig;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.MessageConsumerApplicationInitializer;
import uk.gov.dwp.queue.triage.core.service.processor.FailedMessageProcessor;

import java.util.Optional;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory.NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX;

public class ActiveMqTlsIntegrationTest {

    private static final String TLS_BROKER_NAME = "tls-broker";
    private static final String CONFIGURED_QUEUE = "ActiveMQ.DLQ";
    private static final String FAILED_MESSAGE_PROCESSOR_BEAN_NAME = "failedMessageProcessor";
    private AnnotationConfigApplicationContext applicationContext;

    @Rule
    public TlsEmbeddedActiveMQBroker tlsEmbeddedActiveMQBroker = new TlsEmbeddedActiveMQBroker();

    @After
    public void tearDown() {
        applicationContext.close();
    }

    @Test
    public void createDefaultMessageListenerContainerForSingleBroker() throws JMSException {
        applicationContext = createApplicationContext("tls-broker.yml");

        ActiveMQConnectionFactory connectionFactory = activeMqConnectionFactoryBeanFor(TLS_BROKER_NAME);
        NamedMessageListenerContainer namedMessageListenerContainer = getBean(NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + TLS_BROKER_NAME, NamedMessageListenerContainer.class);
        MyTestFailedMessageProcessor failedMessageProcessor = getBean(FAILED_MESSAGE_PROCESSOR_BEAN_NAME, MyTestFailedMessageProcessor.class);

        assertThat(connectionFactory, instanceOf(ActiveMQSslConnectionFactory.class));
        assertThat(namedMessageListenerContainer, instanceOf(NamedMessageListenerContainer.class));
        assertThat(failedMessageProcessor, instanceOf(FailedMessageProcessor.class));
        String failedMessageContent = "This is a test message over TLS";

        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination queue = session.createQueue(CONFIGURED_QUEUE);

        MessageProducer producer = session.createProducer(queue);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.send(session.createTextMessage(failedMessageContent));

        await().atMost(Duration.FIVE_SECONDS).pollDelay(Duration.FIVE_HUNDRED_MILLISECONDS).until(() -> expectedMessageWithContentIsFound(failedMessageProcessor, failedMessageContent));
    }

    private boolean expectedMessageWithContentIsFound(MyTestFailedMessageProcessor failedMessageProcessor, String expectedFaileMessageContent) {
        return Optional.ofNullable(failedMessageProcessor.getReceivedMessage()).map(msg -> expectedFaileMessageContent.equals(msg.getContent())).orElse(false);
    }

    private ActiveMQConnectionFactory activeMqConnectionFactoryBeanFor(String brokerName) {
        return getBean(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + brokerName, ActiveMQConnectionFactory.class);
    }

    private <T> T getBean(String beanName, Class<T> beanType) {
        try {
            return applicationContext.getBean(beanName, beanType);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private AnnotationConfigApplicationContext createApplicationContext(String yamlFilename) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setEnvironment(createEnvironment(yamlFilename));
        new MessageConsumerApplicationInitializer().initialize(applicationContext);
        applicationContext.register(ConfigurationPropertiesBindingPostProcessor.class);
        applicationContext.register(JmsListenerProperties.class, JmsListenerConfig.class, AdditionalConfig.class);
        applicationContext.refresh();
        return applicationContext;
    }

    private StandardEnvironment createEnvironment(String yamlFilename) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new PropertiesPropertySource("application-properties", loadPropertiesFromYaml(yamlFilename)));
        return environment;
    }

    private Properties loadPropertiesFromYaml(String yamlFilename) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource(yamlFilename));
        return yamlPropertiesFactoryBean.getObject();
    }

    @Configuration
    public static class AdditionalConfig {

        @Bean
        public FailedMessageProcessor failedMessageProcessor() {
            return new MyTestFailedMessageProcessor();
        }

    }

    public static class MyTestFailedMessageProcessor implements FailedMessageProcessor {

        private volatile FailedMessage receivedMessage = null;

        @Override
        public void process(FailedMessage failedMessage) {
            receivedMessage = failedMessage;
        }

        FailedMessage getReceivedMessage() {
            return receivedMessage;
        }

    }
}