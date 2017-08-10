package uk.gov.dwp.queue.triage.core.resend.spring;

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
import uk.gov.dwp.queue.triage.core.jms.spring.SpringMessageSender;
import uk.gov.dwp.queue.triage.core.resend.ResendFailedMessageService;
import uk.gov.dwp.queue.triage.core.resend.ResendScheduledExecutorService;
import uk.gov.dwp.queue.triage.core.resend.spring.configuration.ResendFailedMessageConfiguration;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory.JMS_TEMPLATE_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.spring.SpringMessageSenderBeanDefinitionFactory.SPRING_MESSAGE_SENDER_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.resend.spring.ResendFailedMessageServiceBeanDefinitionFactory.RESEND_FAILED_MESSAGE_SERVICE_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.resend.spring.ResendScheduledExecutorServiceBeanDefinitionFactory.RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX;

public class ResendBeanDefinitionFactoryTest {

    private AnnotationConfigApplicationContext applicationContext;

    @After
    public void tearDown() {
        applicationContext.close();
    }

    @Test
    public void createDefaultMessageListenerContainerForSingleBroker() {
        applicationContext = createApplicationContext("single-broker.yml");

        activeMqConnectionFactoryExistsFor("broker-one");
        jmsTemplateExistsFor("broker-one");
        springMessageSenderExistsFor("broker-one");
    }

    @Test
    public void createDefaultMessageListenerContainerForMultipleBrokers() {

        applicationContext = createApplicationContext("multiple-brokers.yml");

        activeMqConnectionFactoryExistsFor("broker-one");
        jmsTemplateExistsFor("broker-one");
        springMessageSenderExistsFor("broker-one");
        resendFailedMessageServiceExistsFor("broker-one");
        scheduledExecutorExistsFor("broker-one");

        activeMqConnectionFactoryExistsFor("broker-two");
        jmsTemplateExistsFor("broker-two");
        springMessageSenderExistsFor("broker-two");
        resendFailedMessageServiceExistsFor("broker-two");
        scheduledExecutorExistsFor("broker-two");
    }

    private void activeMqConnectionFactoryExistsFor(String brokerName) {
        assertThat(applicationContext.getBean(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + "resender-" + brokerName, ActiveMQConnectionFactory.class),
                is(notNullValue(ActiveMQConnectionFactory.class)));
    }

    private void springMessageSenderExistsFor(String brokerName) {
        assertThat(applicationContext.getBean(SPRING_MESSAGE_SENDER_BEAN_NAME_PREFIX + brokerName, SpringMessageSender.class),
                is(notNullValue(SpringMessageSender.class)));
    }

    private void jmsTemplateExistsFor(String brokerName) {
        assertThat(applicationContext.getBean(JMS_TEMPLATE_BEAN_NAME_PREFIX + brokerName, JmsTemplate.class),
                is(notNullValue(JmsTemplate.class)));
    }

    private void resendFailedMessageServiceExistsFor(String brokerName) {
        assertThat(applicationContext.getBean( RESEND_FAILED_MESSAGE_SERVICE_BEAN_NAME_PREFIX + brokerName, ResendFailedMessageService.class),
                is(notNullValue(ResendFailedMessageService.class)));
    }

    private void scheduledExecutorExistsFor(String brokerName) {
        assertThat(applicationContext.getBean(RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX + brokerName, ResendScheduledExecutorService.class),
                is(notNullValue(ResendScheduledExecutorService.class)));
    }

    private AnnotationConfigApplicationContext createApplicationContext(String yamlFilename) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setEnvironment(createEnvironment(yamlFilename));
        applicationContext.register(ResendFailedMessageConfiguration.class, AdditionalConfig.class);
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
        public FailedMessageSearchService failedMessageSearchService() {
            return mock(FailedMessageSearchService.class);
        }
    }
}