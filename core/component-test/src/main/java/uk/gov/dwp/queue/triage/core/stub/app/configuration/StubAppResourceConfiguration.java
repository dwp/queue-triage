package uk.gov.dwp.queue.triage.core.stub.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.stub.app.repository.MessageClassifierRepository;
import uk.gov.dwp.queue.triage.core.stub.app.resource.StubMessageClassifierResource;

@Configuration
@Import(StubAppRepositoryConfiguration.class)
public class StubAppResourceConfiguration {

    @Bean
    public StubMessageClassifierResource stubPrimerResource(MessageClassifierRepository stubMessageClassifierRepository) {
        return new StubMessageClassifierResource(StubAppJmsConfiguration.BROKER_NAME, stubMessageClassifierRepository);
    }
}
