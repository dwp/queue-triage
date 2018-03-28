package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class NamedMessageListenerContainerBeanDefinitionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamedMessageListenerContainerBeanDefinitionFactory.class);
    public static final String NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX = "namedMessageListenerContainer-";

    public AbstractBeanDefinition create(String brokerName,
                                         String connectionFactoryBeanName,
                                         String queueName,
                                         String failedMessageListenerBeanName) {
        LOGGER.debug("Creating NamedMessageListenerContainer BeanDefinition for {}", brokerName);
        return genericBeanDefinition(NamedMessageListenerContainer.class)
                .addConstructorArgValue(brokerName)
                .addPropertyReference("connectionFactory", connectionFactoryBeanName)
                .addPropertyValue("destinationName", queueName)
                .addPropertyReference("messageListener", failedMessageListenerBeanName)
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + brokerName;
    }
}
