package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserCallback;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserService;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerConfig;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory.QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory.NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory.JMS_TEMPLATE_BEAN_NAME_PREFIX;

public class QueueBrowserBeanDefinitionFactoryTest {

    private AnnotationConfigApplicationContext applicationContext;

    @After
    public void tearDown() {
        applicationContext.close();
    }

    @Test
    public void createDefaultMessageListenerContainerForSingleBroker() {
        applicationContext = createApplicationContext("single-broker-read-only.yml");

        assertThat(activeMqConnectionFactoryBeanFor("broker-one"), is(notNullValue(ActiveMQConnectionFactory.class)));
        assertThat(jmsTemplateBeanFor("broker-one"), is(notNullValue(JmsTemplate.class)));
        assertThat(failedMessageListenerBeanFor("broker-one"), is(notNullValue(FailedMessageListener.class)));
        assertThat(queueBrowserServiceBeanFor("broker-one"), is(notNullValue(QueueBrowserService.class)));
    }

    @Test
    public void createDefaultMessageListenerContainerForMultipleBrokers() {

        applicationContext = createApplicationContext("multiple-brokers-one-read-only.yml");

        assertThat(numberOfBeansOfType(ActiveMQConnectionFactory.class), is(2));
        assertThat(numberOfBeansOfType(FailedMessageListener.class), is(2));
        assertThat(numberOfBeansOfType(JmsTemplate.class), is(1));
        assertThat(numberOfBeansOfType(QueueBrowserCallback.class), is(1));
        assertThat(numberOfBeansOfType(QueueBrowserService.class), is(1));
        assertThat(numberOfBeansOfType(DefaultMessageListenerContainer.class), is(1));

        assertThat(activeMqConnectionFactoryBeanFor("broker-one"), is(notNullValue(ActiveMQConnectionFactory.class)));
        assertThat(jmsTemplateBeanFor("broker-one"), is(notNullValue(JmsTemplate.class)));
        assertThat(failedMessageListenerBeanFor("broker-one"), is(notNullValue(FailedMessageListener.class)));
        assertThat(queueBrowserServiceBeanFor("broker-one"), is(notNullValue(QueueBrowserService.class)));

        assertThat(activeMqConnectionFactoryBeanFor("broker-two"), is(notNullValue(ActiveMQConnectionFactory.class)));
        assertThat(failedMessageListenerBeanFor("broker-two"), is(notNullValue(FailedMessageListener.class)));
        assertThat(defaultMessageListenerContainerBeanFor("broker-two"), is(notNullValue(DefaultMessageListenerContainer.class)));
        assertThat(queueBrowserServiceBeanFor("broker-two"), is(nullValue()));
    }

    private FailedMessageListener failedMessageListenerBeanFor(String brokerName) {
        return getBean(FailedMessageListenerBeanDefinitionFactory.FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX + brokerName, FailedMessageListener.class);
    }

    private ActiveMQConnectionFactory activeMqConnectionFactoryBeanFor(String brokerName) {
        return getBean(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + brokerName, ActiveMQConnectionFactory.class);
    }

    private JmsTemplate jmsTemplateBeanFor(String brokerName) {
        return getBean(JMS_TEMPLATE_BEAN_NAME_PREFIX + brokerName, JmsTemplate.class);
    }

    private QueueBrowserService queueBrowserServiceBeanFor(String brokerName) {
        return getBean(QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX + brokerName, QueueBrowserService.class);
    }

    private DefaultMessageListenerContainer defaultMessageListenerContainerBeanFor(String brokerName) {
        return getBean(NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + brokerName, DefaultMessageListenerContainer.class);
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
        applicationContext.register(JmsListenerConfig.class, AdditionalConfig.class);
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