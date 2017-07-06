package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

@Configuration
public class CoreClientConfiguration {

    @Bean
    public SearchFailedMessageClient searchFailedMessageClient(TestRestTemplate testRestTemplate) {
        return new SearchFailedMessageClient() {
            @Override
            public FailedMessageResponse getFailedMessage(FailedMessageId failedMessageId) {
                return testRestTemplate.getForObject("/core/failed-message/" + failedMessageId, FailedMessageResponse.class);
            }

            @Override
            public long getNumberOfFailedMessages(String broker) {
                return testRestTemplate.getForObject("/core/failed-message/" + broker + "/count", Long.class);
            }
        };
    }

    @Bean
    public CreateFailedMessageClient createFailedMessageClient(TestRestTemplate testRestTemplate) {
        return failedMessageRequest -> testRestTemplate.postForObject("/core/failed-message", failedMessageRequest, String.class);

    }
}
