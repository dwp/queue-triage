package uk.gov.dwp.queue.triage.core.jms.spring;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;

public class JmsTemplateBeanDefinitionFactoryTest {

    private JmsTemplateBeanDefinitionFactory underTest = new JmsTemplateBeanDefinitionFactory();

    @Test
    public void createJmsTemplateBeanDefinition() {
        final AbstractBeanDefinition abstractBeanDefinition = underTest.create("internal-jmsConnectionFactory");

        Assert.assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(JmsTemplate.class));
        Assert.assertThat(abstractBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(), is(1));
        Assert.assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, ConnectionFactory.class).getValue(), is(equalTo(new RuntimeBeanReference("internal-jmsConnectionFactory"))));
    }

    @Test
    public void createJmsTemplateBeanName() {
        assertThat(underTest.createBeanName("internal"), Matchers.is("jmsTemplate-internal"));
    }
}