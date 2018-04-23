package uk.gov.dwp.queue.triage.web.component.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.status.FailedMessageStatusHistoryClient;

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

    @Bean
    public DeleteFailedMessageClient deleteFailedMessageClient() {
        return mock(DeleteFailedMessageClient.class);
    }

    @Bean
    public ResendFailedMessageClient resendFailedMessageClient() {
        return mock(ResendFailedMessageClient.class);
    }

    @Bean
    public FailedMessageStatusHistoryClient failedMessageStatusHistoryClient() {
        return mock(FailedMessageStatusHistoryClient.class);
    }
}
