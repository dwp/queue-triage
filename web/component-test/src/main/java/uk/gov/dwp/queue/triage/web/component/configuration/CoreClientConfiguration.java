package uk.gov.dwp.queue.triage.web.component.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;

import static org.mockito.Mockito.mock;

@Profile("component-test")
@Configuration
public class CoreClientConfiguration {

    @Bean
    public SearchFailedMessageClient searchFailedMessageClient() {
        return mock(SearchFailedMessageClient.class);
    }

    @Bean
    public LabelFailedMessageClient labelFailedMessageClient() {
        return mock(LabelFailedMessageClient.class);
    }
}
