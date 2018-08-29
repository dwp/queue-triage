package uk.gov.dwp.queue.triage.core.stub.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.classification.classifier.ExecutingMessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.predicate.BooleanPredicate;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.stub.app.resource.StubMessageClassifierResource;

@Configuration
@Import(StubAppRepositoryConfiguration.class)
public class StubAppResourceConfiguration {

    @Bean
    public StubMessageClassifierResource stubPrimerResource(MessageClassificationRepository stubMessageClassificationRepository) {
        return new StubMessageClassifierResource(stubMessageClassificationRepository, new ExecutingMessageClassifier(
                new BooleanPredicate(true),
                failedMessage -> {
                    throw new RuntimeException("Head Shot!");
                })
        );
    }
}
