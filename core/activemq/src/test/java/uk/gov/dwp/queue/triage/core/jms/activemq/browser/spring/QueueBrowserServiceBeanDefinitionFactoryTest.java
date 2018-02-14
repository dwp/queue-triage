package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.junit.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserService;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory.QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX;

public class QueueBrowserServiceBeanDefinitionFactoryTest {

    private static final String BROKER_NAME = "brokerName";
    private static final String QUEUE_NAME = "someQueue";

    private QueueBrowserServiceBeanDefinitionFactory underTest =
            new QueueBrowserServiceBeanDefinitionFactory();

    @Test
    public void createQueueBrowserServiceBeanDefinition() {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create("queueBrowserCallback", "messageSender", BROKER_NAME, QUEUE_NAME);

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(QueueBrowserService.class));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(), is(4));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, BrowserCallback.class).getValue(), is(equalTo(new RuntimeBeanReference("queueBrowserCallback"))));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(1, JmsTemplate.class).getValue(), is(equalTo(new RuntimeBeanReference("messageSender"))));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(2, String.class).getValue(), is(BROKER_NAME));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(3, String.class).getValue(), is(QUEUE_NAME));
    }

    @Test
    public void createBeanName() {
        assertThat(underTest.createBeanName(BROKER_NAME), is(equalTo(QUEUE_BROWSER_SERVICE_BEAN_NAME_PREFIX + BROKER_NAME)));
    }

}