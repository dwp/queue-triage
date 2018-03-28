package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class ActiveMQConnectionFactoryBeanDefinitionFactory {

    public static final String ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX = "activeMqConnectionFactory-";

    public AbstractBeanDefinition create(String nameOfFactoryBean) {
        return genericBeanDefinition(ActiveMQConnectionFactory.class)
            .setFactoryMethodOnBean("create", nameOfFactoryBean)
            .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + brokerName;
    }
}
