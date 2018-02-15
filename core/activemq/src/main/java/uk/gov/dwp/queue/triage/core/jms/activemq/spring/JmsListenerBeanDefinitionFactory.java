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
    private final NamedMessageListenerContainerBeanDefinitionFactory namedMessageListenerContainerBeanDefinitionFactory;

    public JmsListenerBeanDefinitionFactory(Environment environment,
                                     ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory,
                                     FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory,
                                     NamedMessageListenerContainerBeanDefinitionFactory namedMessageListenerContainerBeanDefinitionFactory) {
        this.environment = environment;
        this.activeMQConnectionFactoryBeanDefinitionFactory = activeMQConnectionFactoryBeanDefinitionFactory;
        this.failedMessageListenerBeanDefinitionFactory = failedMessageListenerBeanDefinitionFactory;
        this.namedMessageListenerContainerBeanDefinitionFactory = namedMessageListenerContainerBeanDefinitionFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        int index = 0;
        while (hasMoreBrokers(index)) {
            if (!isReadOnly(index)) {
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

                // Create NamedMessageListenerContainer
                registry.registerBeanDefinition(
                        namedMessageListenerContainerBeanDefinitionFactory.createBeanName(brokerName),
                        namedMessageListenerContainerBeanDefinitionFactory.create(
                                brokerName,
                                connectionFactoryBeanName,
                                getProperty(index, "queue"),
                                failedMessageListenerBeanName
                        )
                );
            }
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

    private boolean isReadOnly(int index) {
        return environment.getProperty("jms.activemq.brokers[" + index + "].readOnly", Boolean.class, false);
    }

    private String getProperty(int index, String propertyName) {
        return environment.getProperty("jms.activemq.brokers[" + index + "]." + propertyName);
    }
}
