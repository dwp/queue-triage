package uk.gov.dwp.queue.triage.core.resend.spring.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.SpringMessageSenderBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.resend.HistoricStatusPredicate;
import uk.gov.dwp.queue.triage.core.resend.spring.FailedMessageSenderBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.resend.spring.ResendBeanDefinitionRegistryPostProcessor;
import uk.gov.dwp.queue.triage.core.resend.spring.ResendFailedMessageServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.resend.spring.ResendScheduledExecutorServiceBeanDefinitionFactory;

public class ResendFailedMessageApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final ConfigurableEnvironment environment = applicationContext.getEnvironment();
        applicationContext.addBeanFactoryPostProcessor(new ResendBeanDefinitionRegistryPostProcessor(
                environment,
                new ActiveMQConnectionFactoryBeanDefinitionFactory(),
                new ResendFailedMessageServiceBeanDefinitionFactory(new HistoricStatusPredicate()),
                new FailedMessageSenderBeanDefinitionFactory(),
                new SpringMessageSenderBeanDefinitionFactory(),
                new JmsTemplateBeanDefinitionFactory(),
                new ResendScheduledExecutorServiceBeanDefinitionFactory()
        ));
    }
}
