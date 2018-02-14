package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserService;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class QueueBrowserServiceBeanDefinitionFactory {

    static final String QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX = "queueBrowserService-";

    public AbstractBeanDefinition create(String queueBrowserCallbackBeanName,
                                         String jmsTemplateBeanName,
                                         String brokerName,
                                         String queueName) {
        return genericBeanDefinition(QueueBrowserService.class)
                .addConstructorArgReference(queueBrowserCallbackBeanName)
                .addConstructorArgReference(jmsTemplateBeanName)
                .addConstructorArgValue(brokerName)
                .addConstructorArgValue(queueName)
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX + brokerName;
    }

}
