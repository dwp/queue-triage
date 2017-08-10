package uk.gov.dwp.queue.triage.core.jms.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.jms.core.JmsTemplate;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class JmsTemplateBeanDefinitionFactory {

    public static final String JMS_TEMPLATE_BEAN_NAME_PREFIX = "jmsTemplate-";

    public AbstractBeanDefinition create(String connectionFactoryBeanName) {
        return genericBeanDefinition(JmsTemplate.class)
                .addConstructorArgReference(connectionFactoryBeanName)
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return JMS_TEMPLATE_BEAN_NAME_PREFIX + brokerName;
    }

}
