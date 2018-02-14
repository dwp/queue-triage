package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserCallback;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class QueueBrowserCallbackBeanDefinitionFactory {

    static final String QUEUE_BROWSER_CALLBACK_BEAN_NAME_PREFIX = "queueBrowserCallback-";

    public AbstractBeanDefinition create(String failedMessageListenerBeanName) {
        return genericBeanDefinition(QueueBrowserCallback.class)
                .addConstructorArgReference(failedMessageListenerBeanName)
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return QUEUE_BROWSER_CALLBACK_BEAN_NAME_PREFIX + brokerName;
    }
}
