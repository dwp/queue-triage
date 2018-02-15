package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserScheduledExecutorService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class QueueBrowserScheduledExecutorServiceBeanDefinitionFactory {

    static final String QUEUE_BROWSER_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX = "queueBrowserScheduledExecutorService-";

    public AbstractBeanDefinition create(ScheduledExecutorService executorService,
                                         String queueBrowserServiceBeanName,
                                         long initialDelay,
                                         long executionFrequency,
                                         TimeUnit timeUnit) {
        return genericBeanDefinition(QueueBrowserScheduledExecutorService.class)
                .addConstructorArgValue(executorService)
                .addConstructorArgReference(queueBrowserServiceBeanName)
                .addConstructorArgValue(initialDelay)
                .addConstructorArgValue(executionFrequency)
                .addConstructorArgValue(timeUnit)
                .setInitMethodName("start")
                .setDestroyMethodName("shutdown")
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return QUEUE_BROWSER_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX + brokerName;
    }
}
