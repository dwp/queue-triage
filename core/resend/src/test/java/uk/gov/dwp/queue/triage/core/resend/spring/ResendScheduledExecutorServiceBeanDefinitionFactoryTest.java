package uk.gov.dwp.queue.triage.core.resend.spring;

import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.resend.ResendFailedMessageService;
import uk.gov.dwp.queue.triage.core.resend.ResendScheduledExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.resend.spring.ResendScheduledExecutorServiceBeanDefinitionFactory.RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX;

public class ResendScheduledExecutorServiceBeanDefinitionFactoryTest {

    private static final String BROKER_NAME = "brokerName";

    private final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
    private final ResendScheduledExecutorServiceBeanDefinitionFactory underTest =
            new ResendScheduledExecutorServiceBeanDefinitionFactory();

    @Test
    public void createResendScheduledExecutorServiceBeanDefinition() throws Exception {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(
                executorService,
                "resendFailedMessageService",
                0,
                10,
                SECONDS);

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(ResendScheduledExecutorService.class));
        ConstructorArgumentValues constructorArgumentValues = abstractBeanDefinition.getConstructorArgumentValues();
        assertThat(constructorArgumentValues.getIndexedArgumentValues().size(), is(5));
        assertThat(constructorArgumentValues.getArgumentValue(0, ExecutorService.class).getValue(), is(executorService));
        assertThat(constructorArgumentValues.getArgumentValue(1, ResendFailedMessageService.class).getValue(), is(equalTo(new RuntimeBeanReference("resendFailedMessageService"))));
        assertThat(constructorArgumentValues.getArgumentValue(2, Long.class).getValue(), is(0L));
        assertThat(constructorArgumentValues.getArgumentValue(3, Long.class).getValue(), is(10L));
        assertThat(constructorArgumentValues.getArgumentValue(4, TimeUnit.class).getValue(), is(SECONDS));
    }

    @Test
    public void createBeanName() throws Exception {
        assertThat(underTest.createBeanName(BROKER_NAME), is(equalTo(RESEND_SCHEDULED_EXECUTOR_SERVICE_BEAN_NAME_PREFIX + BROKER_NAME)));
    }
}