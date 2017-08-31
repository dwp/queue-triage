package uk.gov.dwp.queue.triage.core.resend.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.resend.FailedMessageSender;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class FailedMessageSenderBeanDefinitionFactory {

    static final String FAILED_MESSAGE_SENDER_BEAN_NAME_PREFIX = "failedMessageSender-";

    public AbstractBeanDefinition create(String messageSenderDelegate) {
        return genericBeanDefinition(FailedMessageSender.class)
                .addConstructorArgReference(messageSenderDelegate)
                .addConstructorArgReference("failedMessageService")
                .getBeanDefinition();
    }

    public String createBeanName(String brokerName) {
        return FAILED_MESSAGE_SENDER_BEAN_NAME_PREFIX + brokerName;
    }
}
