package uk.gov.dwp.queue.triage.core.jms.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class SpringMessageSenderBeanDefinitionFactory {

    public static final String SPRING_MESSAGE_SENDER_BEAN_NAME_PREFIX = "springMessageSenderBeanDefinitionFactory";

    public AbstractBeanDefinition create(String jmsTemplateBeanName) {
        return genericBeanDefinition(SpringMessageSender.class)
                .addConstructorArgReference(jmsTemplateBeanName)
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return SPRING_MESSAGE_SENDER_BEAN_NAME_PREFIX + brokerName;
    }
}
