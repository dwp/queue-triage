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
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.jms.spring.SpringMessageSender;
import uk.gov.dwp.queue.triage.core.resend.FailedMessageSender;
import uk.gov.dwp.queue.triage.core.resend.ResendFailedMessageService;
import uk.gov.dwp.queue.triage.core.resend.ResendScheduledExecutorService;
import uk.gov.dwp.queue.triage.core.resend.spring.configuration.ResendFailedMessageApplicationInitializer;
import uk.gov.dwp.queue.triage.core.resend.spring.configuration.ResendFailedMessageConfiguration;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import java.util.Arrays;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory.JMS_TEMPLATE_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.jms.spring.SpringMessageSenderBeanDefinitionFactory.SPRING_MESSAGE_SENDER_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.resend.spring.FailedMessageSenderBeanDefinitionFactory.FAILED_MESSAGE_SENDER_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.resend.spring.ResendFailedMessageServiceBeanDefinitionFactory.RESEND_FAILED_MESSAGE_SERVICE_BEAN_NAME_PREFIX;
import static uk.gov.dwp.queue.triage.core.resend.spring.ResendScheduledExecutorServiceBeanDefinitionFactory.RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX;

public class ResendBeanDefinitionRegistryPostProcessorTest {

    private AnnotationConfigApplicationContext applicationContext;

    @After
    public void tearDown() {
        applicationContext.close();
    }

    @Test
    public void createDefaultMessageListenerContainerForSingleBroker() {
        applicationContext = createApplicationContext("single-broker.yml");

        assertAllNecessaryResendingBeansAreCreated("broker-one");
    }

    @Test
    public void createDefaultMessageListenerContainerForMultipleBrokers() {
        applicationContext = createApplicationContext("multiple-brokers.yml");

        for (String brokerName : Arrays.asList("broker-one", "broker-two")) {
            assertAllNecessaryResendingBeansAreCreated(brokerName);
        }
    }

    private void assertAllNecessaryResendingBeansAreCreated(String brokerName) {
        activeMqConnectionFactoryExistsFor(brokerName);
        jmsTemplateExistsFor(brokerName);
        springMessageSenderExistsFor(brokerName);
        failedMessageSenderExistsFor(brokerName);
        resendFailedMessageServiceExistsFor(brokerName);
        scheduledExecutorExistsFor(brokerName);
    }

    private void activeMqConnectionFactoryExistsFor(String brokerName) {
        assertThat(brokerName, applicationContext.getBean(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + "resender-" + brokerName, ActiveMQConnectionFactory.class),
                is(notNullValue(ActiveMQConnectionFactory.class)));
    }

    private void failedMessageSenderExistsFor(String brokerName) {
        assertThat(brokerName, applicationContext.getBean(FAILED_MESSAGE_SENDER_BEAN_NAME_PREFIX + brokerName, FailedMessageSender.class),
                is(notNullValue(FailedMessageSender.class)));
    }

    private void springMessageSenderExistsFor(String brokerName) {
        assertThat(brokerName, applicationContext.getBean(SPRING_MESSAGE_SENDER_BEAN_NAME_PREFIX + brokerName, SpringMessageSender.class),
                is(notNullValue(SpringMessageSender.class)));
    }

    private void jmsTemplateExistsFor(String brokerName) {
        assertThat(brokerName, applicationContext.getBean(JMS_TEMPLATE_BEAN_NAME_PREFIX + brokerName, JmsTemplate.class),
                is(notNullValue(JmsTemplate.class)));
    }

    private void resendFailedMessageServiceExistsFor(String brokerName) {
        assertThat(brokerName, applicationContext.getBean( RESEND_FAILED_MESSAGE_SERVICE_BEAN_NAME_PREFIX + brokerName, ResendFailedMessageService.class),
                is(notNullValue(ResendFailedMessageService.class)));
    }

    private void scheduledExecutorExistsFor(String brokerName) {
        assertThat(brokerName, applicationContext.getBean(RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX + brokerName, ResendScheduledExecutorService.class),
                is(notNullValue(ResendScheduledExecutorService.class)));
    }

    private AnnotationConfigApplicationContext createApplicationContext(String yamlFilename) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setEnvironment(createEnvironment(yamlFilename));
        new ResendFailedMessageApplicationInitializer().initialize(applicationContext);
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

        @Bean
        public FailedMessageService failedMessageService() {
            return mock(FailedMessageService.class);
        }

        @Bean
        public FailedMessageDao failedMessageDao() {
            return mock(FailedMessageDao.class);
        }
    }
}