package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserCallback;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class QueueBrowsingBeanDefinitionFactory implements BeanDefinitionRegistryPostProcessor {

    private final Environment environment;
    private final ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory;
    private final JmsTemplateBeanDefinitionFactory jmsTemplateBeanDefinitionFactory;
    private final FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory;
    private final QueueBrowserCallbackBeanDefinitionFactory queueBrowserCallbackBeanDefinitionFactory;
    private final QueueBrowserServiceBeanDefinitionFactory queueBrowserServiceBeanDefinitionFactory;
    private final QueueBrowserScheduledExecutorServiceBeanDefinitionFactory queueBrowserScheduledExecutorServiceBeanDefinitionFactory;

    public QueueBrowsingBeanDefinitionFactory(Environment environment,
                                              ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory,
                                              JmsTemplateBeanDefinitionFactory jmsTemplateBeanDefinitionFactory,
                                              FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory,
                                              QueueBrowserCallbackBeanDefinitionFactory queueBrowserCallbackBeanDefinitionFactory,
                                              QueueBrowserServiceBeanDefinitionFactory queueBrowserServiceBeanDefinitionFactory,
                                              QueueBrowserScheduledExecutorServiceBeanDefinitionFactory queueBrowserScheduledExecutorServiceBeanDefinitionFactory) {
        this.environment = environment;
        this.activeMQConnectionFactoryBeanDefinitionFactory = activeMQConnectionFactoryBeanDefinitionFactory;
        this.jmsTemplateBeanDefinitionFactory = jmsTemplateBeanDefinitionFactory;
        this.failedMessageListenerBeanDefinitionFactory = failedMessageListenerBeanDefinitionFactory;
        this.queueBrowserCallbackBeanDefinitionFactory = queueBrowserCallbackBeanDefinitionFactory;
        this.queueBrowserServiceBeanDefinitionFactory = queueBrowserServiceBeanDefinitionFactory;
        this.queueBrowserScheduledExecutorServiceBeanDefinitionFactory = queueBrowserScheduledExecutorServiceBeanDefinitionFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        int index = 0;
        while (hasMoreBrokers(index)) {
            String brokerName = getProperty(index, "name");

            // Create ConnectionFactory
            String connectionFactoryBeanName = activeMQConnectionFactoryBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    connectionFactoryBeanName,
                    activeMQConnectionFactoryBeanDefinitionFactory.create(getProperty(index, "url"))
            );

            // Create JmsTemplate
            String jmsTemplateBeanName = jmsTemplateBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    jmsTemplateBeanName,
                    jmsTemplateBeanDefinitionFactory.create(connectionFactoryBeanName)
            );

            // Create MessageListener
            String failedMessageListenerBeanName = failedMessageListenerBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    failedMessageListenerBeanName,
                    failedMessageListenerBeanDefinitionFactory.create(brokerName)
            );

            // Create QueueBrowserCallback
            String queueBrowserCallbackBeanName = queueBrowserCallbackBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    queueBrowserCallbackBeanName,
                    queueBrowserCallbackBeanDefinitionFactory.create(failedMessageListenerBeanName)
            );

            // Create QueueBrowser
            final String queueBrowserServiceBeanName = queueBrowserServiceBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    queueBrowserServiceBeanName,
                    queueBrowserServiceBeanDefinitionFactory.create(
                            queueBrowserCallbackBeanName,
                            jmsTemplateBeanName,
                            brokerName,
                            getProperty(index, "queue")
                    )
            );
            index++;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Do nothing
    }

    private AbstractBeanDefinition createQueueBrowserScheduledExecutorBeanDefinition(int index,
                                                                                     String queueBrowserServiceBeanName) {
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
        return queueBrowserScheduledExecutorServiceBeanDefinitionFactory.create(
                Executors.newSingleThreadScheduledExecutor(),
                queueBrowserServiceBeanName,
                initialDelay,
                executionFrequency,
                timeUnit);
    }

    private boolean hasMoreBrokers(int index) {
        return environment.containsProperty(getPropertyKey(index, "name"));
    }

    private String getProperty(int index, String propertyName) {
        return environment.getProperty(getPropertyKey(index, propertyName));
    }

    private String getPropertyKey(int index, String propertyName) {
        return "jms.activemq.brokers[" + index + "]." + propertyName;
    }
}
