package uk.gov.dwp.queue.triage.core.stub.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.classification.server.repository.memory.InMemoryMessageClassificationRepository;

@Configuration
public class StubAppRepositoryConfiguration {

    @Bean
    public MessageClassificationRepository stubMessageClassificationRepository() {
        return new InMemoryMessageClassificationRepository();
    }
}
