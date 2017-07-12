package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory.ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX;

public class ActiveMQConnectionFactoryBeanDefinitionFactoryTest {

    private static final String BROKER_URL = "some-url";

    private final ActiveMQConnectionFactoryBeanDefinitionFactory underTest = new ActiveMQConnectionFactoryBeanDefinitionFactory();

    @Test
    public void createABeanDefinitionForActiveMqConnectionFactory() throws Exception {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(BROKER_URL);

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(ActiveMQConnectionFactory.class));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(), is(1));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), is(BROKER_URL));
    }

    @Test
    public void createBeanName() throws Exception {
        assertThat(underTest.createBeanName("foo"), is(equalTo(ACTIVE_MQ_CONNECTION_FACTORY_BEAN_NAME_PREFIX + "foo")));
    }
}