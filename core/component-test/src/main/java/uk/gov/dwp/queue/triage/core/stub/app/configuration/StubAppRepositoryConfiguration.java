package uk.gov.dwp.queue.triage.core.stub.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.stub.app.repository.InMemoryMessageClassifierRepository;
import uk.gov.dwp.queue.triage.core.stub.app.repository.MessageClassifierRepository;

@Configuration
public class StubAppRepositoryConfiguration {

    @Bean
    public MessageClassifierRepository stubMessageClassifierRepository() {
        return new InMemoryMessageClassifierRepository();
    }
}
