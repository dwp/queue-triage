package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import uk.gov.dwp.queue.triage.core.jms.JmsMessagePropertyExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessageTextExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQDestinationExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQFailedMessageFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserCallbackBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserScheduledExecutorServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.JmsBeanDefinitionRegistryPostProcessor;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;

public class MessageConsumerApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final ConfigurableEnvironment environment = applicationContext.getEnvironment();
        applicationContext.addBeanFactoryPostProcessor(new JmsBeanDefinitionRegistryPostProcessor(
                environment,
                new ActiveMQConnectionFactoryBeanDefinitionFactory(),
                new FailedMessageListenerBeanDefinitionFactory(brokerName -> new ActiveMQFailedMessageFactory(
                        new MessageTextExtractor(),
                        new ActiveMQDestinationExtractor(brokerName),
                        new JmsMessagePropertyExtractor()
                )),
                new NamedMessageListenerContainerBeanDefinitionFactory(),
                new JmsTemplateBeanDefinitionFactory(),
                new QueueBrowserCallbackBeanDefinitionFactory(),
                new QueueBrowserServiceBeanDefinitionFactory(),
                new QueueBrowserScheduledExecutorServiceBeanDefinitionFactory()
        ));
    }
}
