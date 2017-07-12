package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class DefaultMessageListenerContainerBeanDefinitionFactory {

    public static final String DEFAULT_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX = "defaultMessageListenerContainer-";

    public AbstractBeanDefinition create(String connectionFactoryBeanName,
                                         String queueName,
                                         String failedMessageListenerBeanName) {
        return genericBeanDefinition(DefaultMessageListenerContainer.class)
                .addPropertyReference("connectionFactory", connectionFactoryBeanName)
                .addPropertyValue("destinationName", queueName)
                .addPropertyReference("messageListener", failedMessageListenerBeanName)
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return DEFAULT_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + brokerName;
    }
}
