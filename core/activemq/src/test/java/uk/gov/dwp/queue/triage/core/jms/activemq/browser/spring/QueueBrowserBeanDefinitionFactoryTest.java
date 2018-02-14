package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserService;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.configuration.QueueBrowserConfiguration;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory.QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory.JMS_TEMPLATE_BEAN_NAME_PREFIX;

public class QueueBrowserBeanDefinitionFactoryTest {

    private AnnotationConfigApplicationContext applicationContext;

    @After
    public void tearDown() {
        applicationContext.close();
    }

    @Test
    public void createDefaultMessageListenerContainerForSingleBroker() {
        applicationContext = createApplicationContext("single-broker-browse.yml");

        activeMqConnectionFactoryExistsFor("broker-one");
        jmsTemplateExistsFor("broker-one");
        failedMessageListenerExistsFor("broker-one");
        queueBrowserServiceExistsFor("broker-one");
    }

    @Test
    public void createDefaultMessageListenerContainerForMultipleBrokers() {

        applicationContext = createApplicationContext("multiple-brokers.yml");

        activeMqConnectionFactoryExistsFor("broker-one");
        jmsTemplateExistsFor("broker-one");
        failedMessageListenerExistsFor("broker-one");
        queueBrowserServiceExistsFor("broker-one");

        activeMqConnectionFactoryExistsFor("broker-two");
        jmsTemplateExistsFor("broker-two");
        failedMessageListenerExistsFor("broker-two");
        queueBrowserServiceExistsFor("broker-two");
    }

    private void failedMessageListenerExistsFor(String brokerName) {
        applicationContext.getBean(FailedMessageListenerBeanDefinitionFactory.FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX + brokerName, FailedMessageListener.class);
    }

    private void activeMqConnectionFactoryExistsFor(String brokerName) {
        ActiveMQConnectionFactory activeMQConnectionFactory = applicationContext.getBean(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + brokerName, ActiveMQConnectionFactory.class);
        assertThat(activeMQConnectionFactory, is(notNullValue(ActiveMQConnectionFactory.class)));
    }

    private void jmsTemplateExistsFor(String brokerName) {
        applicationContext.getBean(JMS_TEMPLATE_BEAN_NAME_PREFIX + brokerName, JmsTemplate.class);
    }

    private void queueBrowserServiceExistsFor(String brokerName) {
        applicationContext.getBean(QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX + brokerName, QueueBrowserService.class);
    }

    private AnnotationConfigApplicationContext createApplicationContext(String yamlFilename) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setEnvironment(createEnvironment(yamlFilename));
        applicationContext.register(QueueBrowserConfiguration.class, AdditionalConfig.class);
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

        static String yamlFile = "application-single-broker.yml";

        @Bean
        public FailedMessageService failedMessageService() {
            return mock(FailedMessageService.class);
        }

//        @Bean
//        @Order(Ordered.HIGHEST_PRECEDENCE)
//        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//
//            PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//            propertySourcesPlaceholderConfigurer.setProperties(yamlPropertiesFactoryBean.getObject());
//            return propertySourcesPlaceholderConfigurer;
//        }
    }
}