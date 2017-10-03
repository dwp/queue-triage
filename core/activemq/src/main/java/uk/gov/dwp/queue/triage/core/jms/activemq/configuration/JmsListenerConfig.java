package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.jms.JmsMessagePropertyExtractor;
import uk.gov.dwp.queue.triage.core.jms.MessageTextExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQDestinationExtractor;
import uk.gov.dwp.queue.triage.core.jms.activemq.ActiveMQFailedMessageFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.JmsListenerAdminResource;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.JmsListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Import({
        CxfConfiguration.class
})
public class JmsListenerConfig {

    @Bean
    public static JmsListenerBeanDefinitionFactory jmsListenerBeanDefinitionFactory(Environment environment) {
        return new JmsListenerBeanDefinitionFactory(
                environment,
                new ActiveMQConnectionFactoryBeanDefinitionFactory(),
                new FailedMessageListenerBeanDefinitionFactory(brokerName -> new ActiveMQFailedMessageFactory(
                        new MessageTextExtractor(),
                        new ActiveMQDestinationExtractor(brokerName),
                        new JmsMessagePropertyExtractor()
                )),
                new NamedMessageListenerContainerBeanDefinitionFactory()
        );
    }

    @Bean
    public JmsListenerAdminResource jmsListenerAdminResource(ResourceRegistry resourceRegistry,
                                                             List<NamedMessageListenerContainer> namedMessageListenerContainers) {
        return resourceRegistry.add(new JmsListenerAdminResource(
                namedMessageListenerContainers
                        .stream()
                        .collect(Collectors.toMap(NamedMessageListenerContainer::getBrokerName, Function.identity()))
        ));
    }
}
