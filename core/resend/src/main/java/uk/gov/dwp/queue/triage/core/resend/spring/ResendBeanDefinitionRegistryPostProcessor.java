package uk.gov.dwp.queue.triage.core.resend.spring;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.SpringMessageSenderBeanDefinitionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ResendBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final Environment environment;
    private final ActiveMQConnectionFactoryFactoryBeanDefinitionFactory activeMQConnectionFactoryFactoryBeanDefinitionFactory;
    private final ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory;
    private final ResendFailedMessageServiceBeanDefinitionFactory resendFailedMessageServiceBeanDefinitionFactory;
    private final FailedMessageSenderBeanDefinitionFactory failedMessageSenderBeanDefinitionFactory;
    private final SpringMessageSenderBeanDefinitionFactory springMessageSenderBeanDefinitionFactory;
    private final JmsTemplateBeanDefinitionFactory jmsTemplateBeanDefinitionFactory;
    private final ResendScheduledExecutorServiceBeanDefinitionFactory resendScheduledExecutorServiceBeanDefinitionFactory;

    public ResendBeanDefinitionRegistryPostProcessor(Environment environment,
                                                     ActiveMQConnectionFactoryFactoryBeanDefinitionFactory activeMQConnectionFactoryFactoryBeanDefinitionFactory,
                                                     ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory,
                                                     ResendFailedMessageServiceBeanDefinitionFactory resendFailedMessageServiceBeanDefinitionFactory,
                                                     FailedMessageSenderBeanDefinitionFactory failedMessageSenderBeanDefinitionFactory,
                                                     SpringMessageSenderBeanDefinitionFactory springMessageSenderBeanDefinitionFactory,
                                                     JmsTemplateBeanDefinitionFactory jmsTemplateBeanDefinitionFactory,
                                                     ResendScheduledExecutorServiceBeanDefinitionFactory resendScheduledExecutorServiceBeanDefinitionFactory) {
        this.environment = environment;
        this.activeMQConnectionFactoryFactoryBeanDefinitionFactory = activeMQConnectionFactoryFactoryBeanDefinitionFactory;
        this.activeMQConnectionFactoryBeanDefinitionFactory = activeMQConnectionFactoryBeanDefinitionFactory;
        this.resendFailedMessageServiceBeanDefinitionFactory = resendFailedMessageServiceBeanDefinitionFactory;
        this.failedMessageSenderBeanDefinitionFactory = failedMessageSenderBeanDefinitionFactory;
        this.springMessageSenderBeanDefinitionFactory = springMessageSenderBeanDefinitionFactory;
        this.jmsTemplateBeanDefinitionFactory = jmsTemplateBeanDefinitionFactory;
        this.resendScheduledExecutorServiceBeanDefinitionFactory = resendScheduledExecutorServiceBeanDefinitionFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int index = 0;
        while (hasMoreBrokers(index)) {
            String brokerName = environment.getProperty(getPropertyKey(index, "name"));

            // Create the Factory to create the factory
            String factoryBeanName = activeMQConnectionFactoryFactoryBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                factoryBeanName,
                activeMQConnectionFactoryFactoryBeanDefinitionFactory.create(brokerName)
            );

            // Create ConnectionFactory
            String connectionFactoryBeanName = activeMQConnectionFactoryBeanDefinitionFactory.createBeanName("resender-" + brokerName);
            registry.registerBeanDefinition(
                    connectionFactoryBeanName,
                    activeMQConnectionFactoryBeanDefinitionFactory.create(
                        factoryBeanName)
            );

            // Create JmsTemplate
            String jmsTemplateBeanName = jmsTemplateBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    jmsTemplateBeanName,
                    jmsTemplateBeanDefinitionFactory.create(connectionFactoryBeanName)
            );

            // Create SpringMessageSender
            String springMessageSenderBeanName = springMessageSenderBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    springMessageSenderBeanName,
                    springMessageSenderBeanDefinitionFactory.create(jmsTemplateBeanName)
            );

            // Create FailedMessageSender
            String failedMessageSenderBeanName = failedMessageSenderBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    failedMessageSenderBeanName,
                    failedMessageSenderBeanDefinitionFactory.create(springMessageSenderBeanName)
            );

            // Create a ResendFailedMessageService
            String resendFailedMessageServiceBeanName = resendFailedMessageServiceBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    resendFailedMessageServiceBeanName,
                    resendFailedMessageServiceBeanDefinitionFactory.create(brokerName, failedMessageSenderBeanName)
            );

            // Create a ScheduledExecutor
            registry.registerBeanDefinition(
                    resendScheduledExecutorServiceBeanDefinitionFactory.createBeanName(brokerName),
                    createResendScheduledExecutorBeanDefinition(index, resendFailedMessageServiceBeanName)
            );

            index++;
        }
    }

    private AbstractBeanDefinition createResendScheduledExecutorBeanDefinition(int index,
                                                                               String resendFailedMessageServiceBeanName) {
        Long initialDelay = environment.getProperty(
                getPropertyKey(index, "resend.initialDelay"),
                Long.class,
                0L);
        Long executionFrequency = environment.getProperty(
                getPropertyKey(index, "resend.frequency"),
                Long.class,
                60L);
        TimeUnit timeUnit = environment.getProperty(
                getPropertyKey(index, "resend.unit"),
                TimeUnit.class,
                TimeUnit.SECONDS);
        return resendScheduledExecutorServiceBeanDefinitionFactory.create(
                Executors.newSingleThreadScheduledExecutor(),
                resendFailedMessageServiceBeanName,
                initialDelay,
                executionFrequency,
                timeUnit);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // Do nothing
    }

    private boolean hasMoreBrokers(int index) {
        return environment.containsProperty(getPropertyKey(index, "name"));
    }

    private String getPropertyKey(int index, String propertyName) {
        return "jms.activemq.brokers[" + index + "]." + propertyName;
    }
}
