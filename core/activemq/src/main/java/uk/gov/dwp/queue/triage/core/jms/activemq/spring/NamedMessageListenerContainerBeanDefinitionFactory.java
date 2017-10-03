package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class NamedMessageListenerContainerBeanDefinitionFactory {

    public static final String DEFAULT_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX = "defaultMessageListenerContainer-";

    public AbstractBeanDefinition create(String brokerName,
                                         String connectionFactoryBeanName,
                                         String queueName,
                                         String failedMessageListenerBeanName) {
        return genericBeanDefinition(NamedMessageListenerContainer.class)
                .addConstructorArgValue(brokerName)
                .addPropertyReference("connectionFactory", connectionFactoryBeanName)
                .addPropertyValue("destinationName", queueName)
                .addPropertyReference("messageListener", failedMessageListenerBeanName)
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return DEFAULT_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + brokerName;
    }
}
