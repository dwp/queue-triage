package uk.gov.dwp.queue.triage.core.resend.spring;

import org.junit.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.resend.HistoricStatusPredicate;
import uk.gov.dwp.queue.triage.core.resend.ResendFailedMessageService;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.resend.spring.ResendFailedMessageServiceBeanDefinitionFactory.RESEND_FAILED_MESSAGE_SERVICE_BEAN_NAME_PREFIX;

public class ResendFailedMessageServiceBeanDefinitionFactoryTest {

    private static final String BROKER_NAME = "brokerName";

    private final HistoricStatusPredicate historicStatusPredicate = mock(HistoricStatusPredicate.class);
    private ResendFailedMessageServiceBeanDefinitionFactory underTest =
            new ResendFailedMessageServiceBeanDefinitionFactory(historicStatusPredicate);

    @Test
    public void createResendFailedMessageServiceBeanDefinition() throws Exception {
        AbstractBeanDefinition abstractBeanDefinition = underTest.create(BROKER_NAME, "messageSender");

        assertThat(abstractBeanDefinition.getBeanClass(), typeCompatibleWith(ResendFailedMessageService.class));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(), is(4));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(0, String.class).getValue(), is(BROKER_NAME));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(1, FailedMessageSearchService.class).getValue(), is(equalTo(new RuntimeBeanReference("failedMessageSearchService"))));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(2, MessageSender.class).getValue(), is(equalTo(new RuntimeBeanReference("messageSender"))));
        assertThat(abstractBeanDefinition.getConstructorArgumentValues().getArgumentValue(3, HistoricStatusPredicate.class).getValue(), is(historicStatusPredicate));
    }

    @Test
    public void createBeanName() throws Exception {
        assertThat(underTest.createBeanName(BROKER_NAME), is(equalTo(RESEND_FAILED_MESSAGE_SERVICE_BEAN_NAME_PREFIX + BROKER_NAME)));
    }

}