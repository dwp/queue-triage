package uk.gov.dwp.queue.triage.core.resend.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.resend.ResendScheduledExecutorService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class ResendScheduledExecutorServiceBeanDefinitionFactory {

    static final String RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX = "resendScheduledExecutorService-";

    public AbstractBeanDefinition create(ScheduledExecutorService executorService,
                                         String resendFailedMessageServiceBeanName,
                                         long initialDelay,
                                         long executionFrequency,
                                         TimeUnit timeUnit) {
        return genericBeanDefinition(ResendScheduledExecutorService.class)
                .addConstructorArgValue(executorService)
                .addConstructorArgReference(resendFailedMessageServiceBeanName)
                .addConstructorArgValue(initialDelay)
                .addConstructorArgValue(executionFrequency)
                .addConstructorArgValue(timeUnit)
                .setDestroyMethodName("stop")
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX + brokerName;
    }
}
