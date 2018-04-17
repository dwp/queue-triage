package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserScheduledExecutorService;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerConfig;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.MessageConsumerApplicationInitializer;
import uk.gov.dwp.queue.triage.core.service.processor.FailedMessageProcessor;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserScheduledExecutorServiceBeanDefinitionFactory.QUEUE_BROWSER_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory.FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory.NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX;

public class JmsBeanDefinitionRegistryPostProcessorTest {

    private AnnotationConfigApplicationContext applicationContext;

    @After
    public void tearDown() {
        applicationContext.close();
    }

    @Test
    public void createDefaultMessageListenerContainerForSingleBroker() {
        applicationContext = createApplicationContext("single-broker.yml");

        assertThat(activeMqConnectionFactoryBeanFor("read-write-broker"), is(notNullValue(ActiveMQConnectionFactory.class)));
        assertThat(failedMessageListenerBeanFor("read-write-broker"), is(notNullValue(FailedMessageListener.class)));
        assertThat(defaultMessageListenerContainerBeanFor("read-write-broker"), is(notNullValue(DefaultMessageListenerContainer.class)));

        assertThat(numberOfBeansOfType(ActiveMQConnectionFactory.class), is(1));
        assertThat(numberOfBeansOfType(FailedMessageListener.class), is(1));
        assertThat(numberOfBeansOfType(NamedMessageListenerContainer.class), is(1));
        assertThat(numberOfBeansOfType(QueueBrowserScheduledExecutorService.class), is(0));
    }

    @Test
    public void createOneReadOnlyAndOneDefaultMessageListenerContainerForMultipleBrokers() {

        applicationContext = createApplicationContext("multiple-brokers-one-read-only.yml");

        assertThat(numberOfBeansOfType(ActiveMQConnectionFactory.class), is(2));
        assertThat(numberOfBeansOfType(FailedMessageListener.class), is(2));
        assertThat(numberOfBeansOfType(NamedMessageListenerContainer.class), is(1));
        assertThat(numberOfBeansOfType(QueueBrowserScheduledExecutorService.class), is(1));

        assertThat(activeMqConnectionFactoryBeanFor("read-write-broker"), is(notNullValue(ActiveMQConnectionFactory.class)));
        assertThat(failedMessageListenerBeanFor("read-write-broker"), is(notNullValue(FailedMessageListener.class)));
        assertThat(defaultMessageListenerContainerBeanFor("read-write-broker"), is(notNullValue(DefaultMessageListenerContainer.class)));

        assertThat(activeMqConnectionFactoryBeanFor("read-only-broker"), is(notNullValue(ActiveMQConnectionFactory.class)));
        assertThat(failedMessageListenerBeanFor("read-only-broker"), is(notNullValue(FailedMessageListener.class)));
        assertThat(queueBrowserScheduledExecutorService("read-only-broker"), is(notNullValue(QueueBrowserScheduledExecutorService.class))); 
    }

    private FailedMessageListener failedMessageListenerBeanFor(String brokerName) {
        return getBean(FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX + brokerName, FailedMessageListener.class);
    }

    private DefaultMessageListenerContainer defaultMessageListenerContainerBeanFor(String brokerName) {
        return getBean(NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + brokerName, DefaultMessageListenerContainer.class);
    }

    private ActiveMQConnectionFactory activeMqConnectionFactoryBeanFor(String brokerName) {
        return getBean(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + brokerName, ActiveMQConnectionFactory.class);
    }

    private QueueBrowserScheduledExecutorService queueBrowserScheduledExecutorService(String brokerName) {
        return getBean(QUEUE_BROWSER_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX + brokerName, QueueBrowserScheduledExecutorService.class);
    }

    private <T> T getBean(String beanName, Class<T> beanType) {
        try {
            return applicationContext.getBean(beanName, beanType);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private int numberOfBeansOfType(Class<?> beanType) {
        return applicationContext.getBeanNamesForType(beanType).length;
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

    private Properties loadPropertiesFromYaml(String yamlFilename) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource(yamlFilename));
        return yamlPropertiesFactoryBean.getObject();
    }

    @Configuration
    public static class AdditionalConfig {

        @Bean
        public FailedMessageProcessor failedMessageProcessor() {
            return mock(FailedMessageProcessor.class);
        }

    }
}