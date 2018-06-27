package uk.gov.dwp.queue.triage.core.classification.server.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcomeAdapter;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.classification.server.MessageClassificationService;
import uk.gov.dwp.queue.triage.core.classification.server.executor.MessageClassificationExecutorService;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.classification.server.repository.memory.InMemoryMessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.classification.server.resource.MessageClassificationResource;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseFactory;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.util.concurrent.Executors;

@Configuration
@Import({CxfConfiguration.class, JacksonConfiguration.class})
@EnableConfigurationProperties(MessageClassificationProperties.class)
public class MessageClassificationConfiguration {

    @Bean
    public MessageClassificationResource messageClassificationResource(ResourceRegistry resourceRegistry,
                                                                       MessageClassificationRepository messageClassificationRepository,
                                                                       FailedMessageSearchService failedMessageSearchService,
                                                                       ObjectMapper objectMapper) {
        return resourceRegistry.add(new MessageClassificationResource<>(
                messageClassificationRepository,
                failedMessageSearchService,
                StringDescription::new,
                new MessageClassificationOutcomeAdapter(objectMapper, new FailedMessageResponseFactory()))
        );
    }

    @Bean
    public MessageClassificationRepository messageClassificationRepository() {
        return new InMemoryMessageClassificationRepository();
    }

    @Bean
    public MessageClassificationService messageClassificationService(FailedMessageSearchService failedMessageSearchService,
                                                                     MessageClassificationRepository messageClassificationRepository) {
        return new MessageClassificationService<>(failedMessageSearchService, messageClassificationRepository, StringDescription::new);
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
