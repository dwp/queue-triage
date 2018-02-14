package uk.gov.dwp.queue.triage.core.jms.activemq.browser.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserCallbackBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserScheduledExecutorServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowserServiceBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.spring.QueueBrowsingBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.CommonJmsConfiguration;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.ActiveMQConnectionFactoryBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.spring.FailedMessageListenerBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.core.jms.spring.JmsTemplateBeanDefinitionFactory;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;

@Configuration
@Import({
        CxfConfiguration.class,
        CommonJmsConfiguration.class
})
public class QueueBrowserConfiguration {

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
/*
    @Bean
    public JmsListenerAdminResource jmsListenerAdminResource(ResourceRegistry resourceRegistry,
                                                             List<NamedMessageListenerContainer> namedMessageListenerContainers) {
        return resourceRegistry.add(new JmsListenerAdminResource(
                namedMessageListenerContainers
                        .stream()
                        .collect(Collectors.toMap(NamedMessageListenerContainer::getBrokerName, Function.identity()))
        ));
    }
*/
}
