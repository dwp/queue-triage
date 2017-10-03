package uk.gov.dwp.queue.triage.core.classification.server.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;
import uk.gov.dwp.queue.triage.core.classification.server.MessageClassificationService;
import uk.gov.dwp.queue.triage.core.classification.server.executor.MessageClassificationExecutorService;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.classification.server.repository.memory.InMemoryMessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.classification.server.resource.MessageClassificationResource;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.concurrent.Executors;

@Configuration
@Import(CxfConfiguration.class)
@EnableConfigurationProperties(MessageClassificationProperties.class)
public class MessageClassificationConfiguration {

    @Bean
    public MessageClassificationResource messageClassificationResource(ResourceRegistry resourceRegistry,
                                                                       MessageClassificationRepository messageClassificationRepository) {
        return resourceRegistry.add(new MessageClassificationResource(messageClassificationRepository));
    }

    @Bean
    public MessageClassificationRepository messageClassificationRepository() {
        return new InMemoryMessageClassificationRepository();
    }

    @Bean
    public MessageClassificationService messageClassificationService(FailedMessageSearchService failedMessageSearchService,
                                                                     MessageClassificationRepository messageClassificationRepository) {
        return new MessageClassificationService(failedMessageSearchService, messageClassificationRepository);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MessageClassificationExecutorService messageClassificationExecutorService(MessageClassificationProperties messageClassificationProperties,
                                                                                     MessageClassificationService messageClassificationService,
                                                                                     ResourceRegistry resourceRegistry) {
        return resourceRegistry.add(new MessageClassificationExecutorService(
                Executors.newSingleThreadScheduledExecutor(),
                messageClassificationService,
                messageClassificationProperties.getInitialDelay(),
                messageClassificationProperties.getFrequency(),
                messageClassificationProperties.getTimeUnit()
        ));
    }
}
