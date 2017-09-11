package uk.gov.dwp.queue.triage.core.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;

import java.util.Collections;

@Configuration
@Import(JmsListenerProperties.class)
public class CoreClientConfiguration {

    @Bean
    public CreateFailedMessageClient createFailedMessageClient(TestRestTemplate testRestTemplate) {
        return failedMessageRequest -> testRestTemplate.postForObject(
                "/core/failed-message",
                failedMessageRequest,
                String.class
        );
    }

    @Bean
    public ResendFailedMessageClient resendFailedMessageClient(TestRestTemplate testRestTemplate) {
        return failedMessageId -> testRestTemplate.put(
                "/core/resend/{failedMessageId}",
                null,
                Collections.singletonMap("failedMessageId", failedMessageId)
        );
    }

    @Bean
    public DeleteFailedMessageClient deleteFailedMessageClient(TestRestTemplate testRestTemplate) {
        return failedMessageId -> testRestTemplate.delete(
                "/core/failed-message/{failedMessageId}",
                Collections.singletonMap("failedMessageId", failedMessageId)
        );
    }

    @Bean
    public ActiveMQConnectionFactory testActiveMQConnectionFactory(Environment environment) {
        return new ActiveMQConnectionFactory(environment.getProperty("jms.activemq.brokers[0].url"));
    }

    @Bean
    public JmsTemplate testJmsTemplate(ActiveMQConnectionFactory testActiveMQConnectionFactory) {
        return new JmsTemplate(testActiveMQConnectionFactory);
    }
}
