package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;

@Configuration
public class CoreClientConfiguration {

    @Bean
    public CreateFailedMessageClient createFailedMessageClient(TestRestTemplate testRestTemplate) {
        return failedMessageRequest -> testRestTemplate.postForObject(
                "/core/failed-message",
                failedMessageRequest,
                String.class
        );

    }
}
