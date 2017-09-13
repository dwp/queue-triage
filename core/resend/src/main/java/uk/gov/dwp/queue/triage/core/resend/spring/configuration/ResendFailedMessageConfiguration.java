package uk.gov.dwp.queue.triage.core.resend.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import uk.gov.dwp.migration.mongo.demo.cxf.client.CxfConfiguration;
import uk.gov.dwp.migration.mongo.demo.cxf.client.ResourceRegistry;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.SpringMessageSenderBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.resend.ResendScheduledExecutorsResource;
import uk.gov.dwp.queue.triage.core.resend.spring.FailedMessageSenderBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.resend.spring.ResendBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.resend.spring.ResendFailedMessageServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.resend.spring.ResendScheduledExecutorServiceBeanDefinitionFactory;

@Configuration
@Import({
        CxfConfiguration.class
})
public class ResendFailedMessageConfiguration {

    @Bean
    public static ResendBeanDefinitionFactory resendBeanDefinitionFactory(Environment environment) {
        return new ResendBeanDefinitionFactory(
                environment,
                new ActiveMQConnectionFactoryBeanDefinitionFactory(),
                new ResendFailedMessageServiceBeanDefinitionFactory(),
                new FailedMessageSenderBeanDefinitionFactory(),
                new SpringMessageSenderBeanDefinitionFactory(),
                new JmsTemplateBeanDefinitionFactory(),
                new ResendScheduledExecutorServiceBeanDefinitionFactory()
        );
    }

    @Bean
    public ResendScheduledExecutorsResource resendScheduledExecutorsResource(ResourceRegistry resourceRegistry) {
        return resourceRegistry.add(new ResendScheduledExecutorsResource());
    }
}
