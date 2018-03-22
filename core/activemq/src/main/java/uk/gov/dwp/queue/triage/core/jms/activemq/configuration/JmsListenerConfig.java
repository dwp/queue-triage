package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageConsumerManager;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageConsumerManagerRegistry;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageConsumerManagerResource;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Import({CxfConfiguration.class})
public class JmsListenerConfig {

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
