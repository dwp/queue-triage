package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.DefaultMessageListenerContainerBeanDefinitionFactory.DEFAULT_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX;

public class DefaultMessageListenerContainerBeanDefinitionFactoryTest {

    private static final String CONNECTION_FACTORY_BEAN_NAME = "connectionFactoryBeanName";
    private static final String QUEUE_NAME = "queueName";
    private static final String FAILED_MESSAGE_LISTENER_BEAN_NAME = "failedMessageListenerBeanName";

    private final DefaultMessageListenerContainerBeanDefinitionFactory underTest =
            new DefaultMessageListenerContainerBeanDefinitionFactory();

    @Test
    public void createBeanDefinitionForDefaultMessageListenerContainer() throws Exception {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(
                CONNECTION_FACTORY_BEAN_NAME,
                QUEUE_NAME,
                FAILED_MESSAGE_LISTENER_BEAN_NAME
        );

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(DefaultMessageListenerContainer.class));
        MutablePropertyValues propertyValues = abstractBeanDefinition.getPropertyValues();
        assertThat(propertyValues.size(), is(3));
        assertThat(propertyValues.getPropertyValue("connectionFactory").getValue(), is(equalTo(new RuntimeBeanReference(CONNECTION_FACTORY_BEAN_NAME))));
        assertThat(propertyValues.getPropertyValue("destinationName").getValue(), is(QUEUE_NAME));
        assertThat(propertyValues.getPropertyValue("messageListener").getValue(), is(equalTo(new RuntimeBeanReference(FAILED_MESSAGE_LISTENER_BEAN_NAME))));
    }

    @Test
    public void createBeanName() throws Exception {
        assertThat(underTest.createBeanName("foo"), is(equalTo(DEFAULT_MESSAGE_LISTENER_CONTAINER_BEAN_NAME_PREFIX + "foo")));
    }

}