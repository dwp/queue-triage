package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.junit.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryFactoryBeanDefinitionFactory.FACTORY_BEAN_NAME_PREFIX;

public class ActiveMQConnectionFactoryFactoryBeanDefinitionFactoryTest {

    private static final String BROKER_NAME = "brokerName";

    private final ActiveMQConnectionFactoryFactoryBeanDefinitionFactory underTest = new ActiveMQConnectionFactoryFactoryBeanDefinitionFactory();

    @Test
    public void createABeanDefinitionForActiveMqConnectionFactory() throws Exception {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(BROKER_NAME);

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(ActiveMQConnectionFactoryFactory.class));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), is(BROKER_NAME));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(1, JmsListenerProperties.class, "jmsListenerProperties").getValue(), is(new RuntimeBeanReference("jmsListenerProperties")));
    }

    @Test
    public void createBeanName() throws Exception {
        assertThat(underTest.createBeanName("foo"), is(equalTo(FACTORY_BEAN_NAME_PREFIX + "foo")));
    }

}