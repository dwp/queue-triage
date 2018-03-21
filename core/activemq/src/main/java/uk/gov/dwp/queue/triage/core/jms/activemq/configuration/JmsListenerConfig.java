package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageConsumerManager;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageConsumerManagerRegistry;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageConsumerManagerResource;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserCallbackBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserScheduledExecutorServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowsingBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.JmsListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;
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
    public static JmsListenerBeanDefinitionFactory jmsListenerBeanDefinitionFactory(Environment environment,
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
    public static QueueBrowsingBeanDefinitionFactory queueBrowsingBeanDefinitionFactory(Environment environment,
                                                                                        ActiveMQConnectionFactoryBeanDefinitionFactory activeMQConnectionFactoryBeanDefinitionFactory,
                                                                                        FailedMessageListenerBeanDefinitionFactory failedMessageListenerBeanDefinitionFactory) {
        return new QueueBrowsingBeanDefinitionFactory(
                environment,
                activeMQConnectionFactoryBeanDefinitionFactory,
                new JmsTemplateBeanDefinitionFactory(),
                failedMessageListenerBeanDefinitionFactory,
                new QueueBrowserCallbackBeanDefinitionFactory(), new QueueBrowserServiceBeanDefinitionFactory(),
                new QueueBrowserScheduledExecutorServiceBeanDefinitionFactory());
    }

    @Bean
    public MessageConsumerManagerRegistry messageConsumerManagerRegistry(List<MessageConsumerManager> messageConsumerManagers) {
        return new MessageConsumerManagerRegistry(messageConsumerManagers
                .stream()
                .collect(Collectors.toMap(MessageConsumerManager::getBrokerName, Function.identity()))
        );
    }

    @Bean
    public MessageConsumerManagerResource messageListenerManagerResource(ResourceRegistry resourceRegistry,
                                                                         MessageConsumerManagerRegistry messageConsumerManagerRegistry) {
        return resourceRegistry.add(new MessageConsumerManagerResource(messageConsumerManagerRegistry));
    }
}
