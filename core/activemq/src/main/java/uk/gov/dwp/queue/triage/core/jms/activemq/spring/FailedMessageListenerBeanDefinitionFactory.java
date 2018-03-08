package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQFailedMessageFactory;

import java.util.function.Function;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class FailedMessageListenerBeanDefinitionFactory {

    public static final String FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX = "failedMessageListener-";
    private final Function<String, ActiveMQFailedMessageFactory> activeMQFailedMessageFactoryFactory;

    public FailedMessageListenerBeanDefinitionFactory(Function<String, ActiveMQFailedMessageFactory> activeMQFailedMessageFactoryFactory) {
        this.activeMQFailedMessageFactoryFactory = activeMQFailedMessageFactoryFactory;
    }

    public AbstractBeanDefinition create(String brokerName) {
        return genericBeanDefinition(FailedMessageListener.class)
                .addConstructorArgValue(activeMQFailedMessageFactoryFactory.apply(brokerName))
                .addConstructorArgReference("failedMessageProcessor")
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX + brokerName;
    }
}
