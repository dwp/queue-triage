package uk.gov.dwp.queue.triage.core.resend.spring;

import org.junit.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.resend.FailedMessageSender;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.resend.spring.FailedMessageSenderBeanDefinitionFactory.FAILED_MESSAGE_SENDER_BEAN_NAME_PREFIX;

public class FailedMessageSenderBeanDefinitionFactoryTest {

    private static final String BROKER_NAME = "brokerName";

    private final FailedMessageSenderBeanDefinitionFactory underTest = new FailedMessageSenderBeanDefinitionFactory();

    @Test
    public void createFailedMessageSenderBeanDefinition() throws Exception {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create("messageSenderDelegate");

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(FailedMessageSender.class));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(), is(2));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, MessageSender.class).getValue(), is(equalTo(new RuntimeBeanReference("messageSenderDelegate"))));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(1, FailedMessageService.class).getValue(), is(equalTo(new RuntimeBeanReference("failedMessageService"))));
    }

    @Test
    public void createBeanName() {
        assertThat(underTest.createBeanName(BROKER_NAME), is(equalTo(FAILED_MESSAGE_SENDER_BEAN_NAME_PREFIX + BROKER_NAME)));
    }
}