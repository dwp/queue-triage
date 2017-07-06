package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.env.Environment;

public class JmsListenerBeanDefinitionFactory implements BeanDefinitionRegistryPostProcessor {

    private final Environment environment;
    private final ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory;
    private final FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory;
    private final DefaultMessageListenerContainerBeanDefinitionFactory defaultMessageListenerContainerBeanDefinitionFactory;

    public JmsListenerBeanDefinitionFactory(Environment environment,
                                     ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory,
                                     FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory,
                                     DefaultMessageListenerContainerBeanDefinitionFactory defaultMessageListenerContainerBeanDefinitionFactory) {
        this.environment = environment;
        this.activeMQConnectionFactoryBeanDefinitionFactory = activeMQConnectionFactoryBeanDefinitionFactory;
        this.failedMessageListenerBeanDefinitionFactory = failedMessageListenerBeanDefinitionFactory;
        this.defaultMessageListenerContainerBeanDefinitionFactory = defaultMessageListenerContainerBeanDefinitionFactory;
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

            // Create MessageListener
            String failedMessageListenerBeanName = failedMessageListenerBeanDefinitionFactory.createBeanName(brokerName);
            registry.registerBeanDefinition(
                    failedMessageListenerBeanName,
                    failedMessageListenerBeanDefinitionFactory.create(brokerName)
            );

            // Create DefaultMessageListenerContainer
            registry.registerBeanDefinition(
                    defaultMessageListenerContainerBeanDefinitionFactory.createBeanName(brokerName),
                    defaultMessageListenerContainerBeanDefinitionFactory.create(
                            connectionFactoryBeanName,
                            getProperty(index, "queue"),
                            failedMessageListenerBeanName
                    )
            );
            index++;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Do nothing
    }

    private boolean hasMoreBrokers(int index) {
        return environment.containsProperty("jms.activemq.brokers[" + index + "].name");
    }

    private String getProperty(int index, String propertyName) {
        return environment.getProperty("jms.activemq.brokers[" + index + "]." + propertyName);
    }
}
