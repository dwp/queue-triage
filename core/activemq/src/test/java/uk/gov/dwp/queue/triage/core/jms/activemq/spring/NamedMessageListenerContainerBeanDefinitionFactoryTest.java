package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory.NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX;

public class NamedMessageListenerContainerBeanDefinitionFactoryTest {

    private static final String CONNECTION_FACTORY_BEAN_NAME = "connectionFactoryBeanName";
    private static final String QUEUE_NAME = "queueName";
    private static final String FAILED_MESSAGE_LISTENER_BEAN_NAME = "failedMessageListenerBeanName";
    private static final String BROKER_NAME = "broker-name";

    private final NamedMessageListenerContainerBeanDefinitionFactory underTest =
            new NamedMessageListenerContainerBeanDefinitionFactory();

    @Test
    public void createBeanDefinitionForDefaultMessageListenerContainer() {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(
                BROKER_NAME,
                CONNECTION_FACTORY_BEAN_NAME,
                QUEUE_NAME,
                FAILED_MESSAGE_LISTENER_BEAN_NAME
        );

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(NamedMessageListenerContainer.class));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(), is(1));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), is(BROKER_NAME));
        MutablePropertyValues propertyValues = abstractBeanDefinition.getPropertyValues();
        assertThat(propertyValues.size(), is(3));
        assertThat(propertyValues.getPropertyValue("connectionFactory").getValue(), is(equalTo(new RuntimeBeanReference(CONNECTION_FACTORY_BEAN_NAME))));
        assertThat(propertyValues.getPropertyValue("destinationName").getValue(), is(QUEUE_NAME));
        assertThat(propertyValues.getPropertyValue("messageListener").getValue(), is(equalTo(new RuntimeBeanReference(FAILED_MESSAGE_LISTENER_BEAN_NAME))));
    }

    @Test
    public void createBeanName() throws Exception {
        assertThat(underTest.createBeanName("foo"), is(equalTo(NAMED_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + "foo")));
    }

}