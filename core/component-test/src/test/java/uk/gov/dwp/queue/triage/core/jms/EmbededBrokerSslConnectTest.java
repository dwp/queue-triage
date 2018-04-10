package uk.gov.dwp.queue.triage.core.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.junit.TlsEmbeddedActiveMQBroker;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerConfig;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.MessageConsumerApplicationInitializer;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;

public class EmbededBrokerSslConnectTest {

    private static Logger LOGGER = LoggerFactory.getLogger(EmbededBrokerSslConnectTest.class);

    @Rule
    public TlsEmbeddedActiveMQBroker embeddedActiveMQBroker = new TlsEmbeddedActiveMQBroker();

    private AnnotationConfigApplicationContext applicationContext;

    @After
    public void tearDown() {
        applicationContext.close();
    }

    @Test
    public void foo() throws JMSException, InterruptedException {
        applicationContext = createApplicationContext("application-tls-component-test.yml");

        final ActiveMQConnectionFactory connectionFactory = activeMqConnectionFactoryBeanFor("internal-broker");
        final Thread consumer = new Thread(() -> {
            try {
                Connection consumerConnection = connectionFactory.createConnection();
                consumerConnection.start();
                Session consumerSession = consumerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final MessageConsumer consumer1 = consumerSession.createConsumer(new ActiveMQQueue("bob"));
                final TextMessage textMessage = (TextMessage) consumer1.receive();
                LOGGER.info("Received message with content: {}", textMessage.getText());
                assertThat(textMessage.getText(), is("Hello"));
                consumer1.close();
                consumerSession.close();
                consumerConnection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        consumer.start();

        Connection producerConnection = connectionFactory.createConnection();
        Session producerSession = producerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = producerSession.createProducer(new ActiveMQQueue("bob"));
        producer.send(producerSession.createTextMessage("Hello"));
        producer.close();
        producerSession.close();
        producerConnection.close();

        consumer.join();
    }

    private ActiveMQConnectionFactory activeMqConnectionFactoryBeanFor(String brokerName) {
        return applicationContext.getBean(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + brokerName, ActiveMQConnectionFactory.class);
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
        environment.getPropertySources().addFirst(new PropertiesPropertySource("broker-properties", loadPropertiesFromYaml(yamlFilename)));
        return environment;
    }

    public Properties loadPropertiesFromYaml(String yamlFilename) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource(yamlFilename));
        return yamlPropertiesFactoryBean.getObject();
    }

    @Configuration
    public static class AdditionalConfig {

        @Bean
        public FailedMessageService failedMessageService() {
            return mock(FailedMessageService.class);
        }

    }
}