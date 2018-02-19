package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.jms.JmsMessagePropertyExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessageTextExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQDestinationExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQFailedMessageFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;

@Configuration
public class CommonJmsConfiguration {

    @Bean
    public static ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory() {
        return new ActiveMQConnectionFactoryBeanDefinitionFactory();
    }

    @Bean
    public static FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory() {
        return new FailedMessageListenerBeanDefinitionFactory(brokerName -> new ActiveMQFailedMessageFactory(
                new MessageTextExtractor(),
                new ActiveMQDestinationExtractor(brokerName),
                new JmsMessagePropertyExtractor()
        ));
    }
}
