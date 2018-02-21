package uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring;

import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserScheduledExecutorService;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserScheduledExecutorServiceBeanDefinitionFactory.QUEUE_BROWSER_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX;

public class QueueBrowserScheduledExecutorServiceBeanDefinitionFactoryTest {

    private static final String BROKER_NAME = "brokerName";

    private final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
    private final QueueBrowserScheduledExecutorServiceBeanDefinitionFactory underTest =
            new QueueBrowserScheduledExecutorServiceBeanDefinitionFactory();

    @Test
    public void createResendScheduledExecutorServiceBeanDefinition(){
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(
                executorService,
                "queueBrowserService",
                0,
                10,
                SECONDS);

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(QueueBrowserScheduledExecutorService.class));
        ConstructorArgumentValues constructorArgumentValues = abstractBeanDefinition.getConstructorArgumentValues();
        assertThat(constructorArgumentValues.getIndexedArgumentValues().size(), is(5));
        assertThat(constructorArgumentValues.getArgumentValue(0, ExecutorService.class).getValue(), is(executorService));
        assertThat(constructorArgumentValues.getArgumentValue(1, QueueBrowserService.class).getValue(), is(equalTo(new RuntimeBeanReference("queueBrowserService"))));
        assertThat(constructorArgumentValues.getArgumentValue(2, Long.class).getValue(), is(0L));
        assertThat(constructorArgumentValues.getArgumentValue(3, Long.class).getValue(), is(10L));
        assertThat(constructorArgumentValues.getArgumentValue(4, TimeUnit.class).getValue(), is(SECONDS));
    }

    @Test
    public void createBeanName() throws Exception {
        assertThat(underTest.createBeanName(BROKER_NAME), is(equalTo(QUEUE_BROWSER_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX + BROKER_NAME)));
    }
}