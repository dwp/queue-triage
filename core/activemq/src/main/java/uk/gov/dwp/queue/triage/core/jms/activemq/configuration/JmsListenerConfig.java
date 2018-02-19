package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageListenerManager;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageListenerManagerResource;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserScheduledExecutorService;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserCallbackBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserScheduledExecutorServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowsingBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.JmsListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainer;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.NamedMessageListenerContainerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public MessageListenerManagerResource messageListenerManagerResource(ResourceRegistry resourceRegistry,
                                                                         Optional<List<NamedMessageListenerContainer>> namedMessageListenerContainers,
                                                                         Optional<List<QueueBrowserScheduledExecutorService>> queueBrowserScheduledExecutorServices) {
        final Map<String, MessageListenerManager> messageListenerMap = namedMessageListenerContainers
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(MessageListenerManager::getBrokerName, Function.identity()));
        messageListenerMap.putAll(queueBrowserScheduledExecutorServices
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(MessageListenerManager::getBrokerName, Function.identity()))
        );
        return resourceRegistry.add(new MessageListenerManagerResource(messageListenerMap));
    }
}
