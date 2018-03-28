package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class ActiveMQConnectionFactoryFactoryBeanDefinitionFactory {

    static final String FACTORY_BEAN_NAME_PREFIX = "activeMqConnectionFactoryFactory-";

    public AbstractBeanDefinition create(String brokerName) {
        return genericBeanDefinition(ActiveMQConnectionFactoryFactory.class)
            .addConstructorArgValue(brokerName)
            .addConstructorArgReference("jmsListenerProperties")
            .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return FACTORY_BEAN_NAME_PREFIX + brokerName;
    }
}
