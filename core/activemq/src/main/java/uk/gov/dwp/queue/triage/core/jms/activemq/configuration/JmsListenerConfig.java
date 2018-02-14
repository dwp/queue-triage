package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
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
        CxfConfiguration.class,
        CommonJmsConfiguration.class
})
public class JmsListenerConfig {

    @Bean
    public JmsListenerBeanDefinitionFactory jmsListenerBeanDefinitionFactory(Environment environment,
                                                                             ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory,
                                                                             FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory) {
        return new JmsListenerBeanDefinitionFactory(
                environment,
                activeMQConnectionFactoryBeanDefinitionFactory,
                failedMessageListenerBeanDefinitionFactory,
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
