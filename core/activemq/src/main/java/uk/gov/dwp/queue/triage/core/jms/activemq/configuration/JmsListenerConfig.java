package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.jms.JmsMessagePropertyExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessageTextExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQDestinationExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQFailedMessageFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.DefaultMessageListenerContainerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.JmsListenerBeanDefinitionFactory;

@Configuration
@Import({
        JmsListenerProperties.class
})
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class JmsListenerConfig {

    @Bean
    public static JmsListenerBeanDefinitionFactory jmsListenerBeanDefinitionFactory(JmsListenerProperties jmsListenerProperties,
                                                                             Environment environment) {
        return new JmsListenerBeanDefinitionFactory(
                environment,
                new ActiveMQConnectionFactoryBeanDefinitionFactory(),
                new FailedMessageListenerBeanDefinitionFactory(brokerName -> new ActiveMQFailedMessageFactory(
                        new MessageTextExtractor(),
                        new ActiveMQDestinationExtractor(brokerName),
                        new JmsMessagePropertyExtractor()
                )),
                new DefaultMessageListenerContainerBeanDefinitionFactory()
        );
    }
}
