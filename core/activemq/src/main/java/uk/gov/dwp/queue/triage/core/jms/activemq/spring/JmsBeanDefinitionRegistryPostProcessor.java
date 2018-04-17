package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.env.Environment;

import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserCallbackBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserScheduledExecutorServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JmsBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsBeanDefinitionRegistryPostProcessor.class);

    private final Environment environment;
    private final ActiveMQConnectionFactoryFactoryBeanDefinitionFactory activeMQConnectionFactoryFactoryBeanDefinitionFactory;
    private final ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory;
    private final FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory;
    private final NamedMessageListenerContainerBeanDefinitionFactory namedMessageListenerContainerBeanDefinitionFactory;
    private final JmsTemplateBeanDefinitionFactory jmsTemplateBeanDefinitionFactory;
    private final QueueBrowserCallbackBeanDefinitionFactory queueBrowserCallbackBeanDefinitionFactory;
    private final QueueBrowserServiceBeanDefinitionFactory queueBrowserServiceBeanDefinitionFactory;
    private final QueueBrowserScheduledExecutorServiceBeanDefinitionFactory queueBrowserScheduledExecutorServiceBeanDefinitionFactory;

    public JmsBeanDefinitionRegistryPostProcessor(Environment environment,
                                                  ActiveMQConnectionFactoryFactoryBeanDefinitionFactory activeMQConnectionFactoryFactoryBeanDefinitionFactory,
                                                  ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory,
                                                  FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory,
                                                  NamedMessageListenerContainerBeanDefinitionFactory namedMessageListenerContainerBeanDefinitionFactory,
                                                  JmsTemplateBeanDefinitionFactory jmsTemplateBeanDefinitionFactory,
                                                  QueueBrowserCallbackBeanDefinitionFactory queueBrowserCallbackBeanDefinitionFactory,
                                                  QueueBrowserServiceBeanDefinitionFactory queueBrowserServiceBeanDefinitionFactory,
                                                  QueueBrowserScheduledExecutorServiceBeanDefinitionFactory queueBrowserScheduledExecutorServiceBeanDefinitionFactory) {
        this.environment = environment;
        this.activeMQConnectionFactoryFactoryBeanDefinitionFactory = activeMQConnectionFactoryFactoryBeanDefinitionFactory;
        this.activeMQConnectionFactoryBeanDefinitionFactory = activeMQConnectionFactoryBeanDefinitionFactory;
        this.failedMessageListenerBeanDefinitionFactory = failedMessageListenerBeanDefinitionFactory;
        this.namedMessageListenerContainerBeanDefinitionFactory = namedMessageListenerContainerBeanDefinitionFactory;
        this.jmsTemplateBeanDefinitionFactory = jmsTemplateBeanDefinitionFactory;
        this.queueBrowserCallbackBeanDefinitionFactory = queueBrowserCallbackBeanDefinitionFactory;
        this.queueBrowserServiceBeanDefinitionFactory = queueBrowserServiceBeanDefinitionFactory;
        this.queueBrowserScheduledExecutorServiceBeanDefinitionFactory = queueBrowserScheduledExecutorServiceBeanDefinitionFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int index = 0;
        while (hasMoreBrokers(index)) {
            String brokerName = getProperty(index, "name");

            // Create the Factory to create the factory
            String factoryBeanName = activeMQConnectionFactoryFactoryBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                factoryBeanName,
                activeMQConnectionFactoryFactoryBeanDefinitionFactory.create(brokerName)
            );

            // Create ConnectionFactory
            String connectionFactoryBeanName = activeMQConnectionFactoryBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                connectionFactoryBeanName,
                activeMQConnectionFactoryBeanDefinitionFactory.create(factoryBeanName)
            );

            // Create MessageListener
            String failedMessageListenerBeanName = failedMessageListenerBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                failedMessageListenerBeanName,
                failedMessageListenerBeanDefinitionFactory.create(brokerName)
            );

            if (isBrokerReadOnly(index)) {
                createReadOnlyMessageConsumer(registry, index, brokerName, connectionFactoryBeanName, failedMessageListenerBeanName);
            } else {
                // Create NamedMessageListenerContainer
                final String queueName = getProperty(index, "queue");
                registry.registerBeanDefinition(
                    namedMessageListenerContainerBeanDefinitionFactory.createBeanName(brokerName),
                    namedMessageListenerContainerBeanDefinitionFactory.create(
                        brokerName,
                        connectionFactoryBeanName,
                        queueName,
                        failedMessageListenerBeanName
                    )
                );
                LOGGER.info("MessageConsumer configured for {} on {}", queueName, brokerName);
            }
            index++;
        }
    }

    private void createReadOnlyMessageConsumer(BeanDefinitionRegistry registry, int index, String brokerName, String connectionFactoryBeanName, String failedMessageListenerBeanName) {
        // Create QueueBrowserCallback
        String queueBrowserCallbackBeanName = queueBrowserCallbackBeanDefinitionFactory.createBeanName(brokerName);
        registry.registerBeanDefinition(
            queueBrowserCallbackBeanName,
            queueBrowserCallbackBeanDefinitionFactory.create(failedMessageListenerBeanName)
        );

        // Create JmsTemplate
        String jmsTemplateBeanName = jmsTemplateBeanDefinitionFactory.createBeanName(brokerName);
        registry.registerBeanDefinition(
            jmsTemplateBeanName,
            jmsTemplateBeanDefinitionFactory.create(connectionFactoryBeanName)
        );

        // Create QueueBrowser
        final String queueBrowserServiceBeanName = queueBrowserServiceBeanDefinitionFactory.createBeanName(brokerName);
        final String queueName = getProperty(index, "queue");
        registry.registerBeanDefinition(
            queueBrowserServiceBeanName,
            queueBrowserServiceBeanDefinitionFactory.create(
                queueBrowserCallbackBeanName,
                jmsTemplateBeanName,
                brokerName,
                queueName
            )
        );
        LOGGER.info("Read-only MessageConsumer configured for {} on {}", queueName, brokerName);

        // Create QueueBrowserScheduledExecutor
        registry.registerBeanDefinition(
            queueBrowserScheduledExecutorServiceBeanDefinitionFactory.createBeanName(brokerName),
            createQueueBrowserScheduledExecutorBeanDefinition(index, queueBrowserServiceBeanName)
        );
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
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

    private boolean isBrokerReadOnly(int index) {
        return environment.getProperty(getPropertyKey(index, "readOnly"), Boolean.class, false);
    }

    private String getProperty(int index, String propertyName) {
        return environment.getProperty(getPropertyKey(index, propertyName));
    }

    private String getPropertyKey(int index, String propertyName) {
        return String.format("jms.activemq.brokers[%d].%s", index, propertyName);
    }
}
