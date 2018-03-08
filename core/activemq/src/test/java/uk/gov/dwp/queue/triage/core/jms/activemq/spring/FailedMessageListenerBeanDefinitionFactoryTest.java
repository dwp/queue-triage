package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.junit.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.FailedMessageListener;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQFailedMessageFactory;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory.FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX;

public class FailedMessageListenerBeanDefinitionFactoryTest {

    private static final String BROKER_NAME = "brokerName";

    private final ActiveMQFailedMessageFactory activeMQFailedMessageFactory = mock(ActiveMQFailedMessageFactory.class);

    private FailedMessageListenerBeanDefinitionFactory underTest =
            new FailedMessageListenerBeanDefinitionFactory(brokerName -> activeMQFailedMessageFactory);

    @Test
    public void createFailedMessageListenerBeanDefinition() {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(BROKER_NAME);

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(FailedMessageListener.class));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(), is(2));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, ActiveMQFailedMessageFactory.class).getValue(), is(activeMQFailedMessageFactory));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(1, ActiveMQFailedMessageFactory.class).getValue(), is(equalTo(new RuntimeBeanReference("failedMessageProcessor"))));
    }

    @Test
    public void createBeanName() {
        assertThat(underTest.createBeanName(BROKER_NAME), is(equalTo(FAILED_MESSAGE_LISTENER_BEAN_NAME_PREFIX + BROKER_NAME)));
    }
}