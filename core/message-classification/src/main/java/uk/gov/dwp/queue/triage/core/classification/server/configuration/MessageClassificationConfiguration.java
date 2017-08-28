package uk.gov.dwp.queue.triage.core.classification.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.migration.mongo.demo.cxf.client.CxfConfiguration;
import uk.gov.dwp.migration.mongo.demo.cxf.client.ResourceRegistry;
import uk.gov.dwp.queue.triage.core.classification.server.MessageClassificationService;
import uk.gov.dwp.queue.triage.core.classification.server.executor.MessageClassificationExecutorService;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.classification.server.repository.memory.InMemoryMessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.classification.server.resource.MessageClassificationResource;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@Import({
        CxfConfiguration.class
})
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
    public MessageClassificationExecutorService messageClassificationExecutorService(MessageClassificationService messageClassificationService,
                                                                                     ResourceRegistry resourceRegistry) {
        return resourceRegistry.add(new MessageClassificationExecutorService(
                Executors.newSingleThreadScheduledExecutor(),
                messageClassificationService,
                0,
                60,
                TimeUnit.SECONDS
        ));
    }
}
