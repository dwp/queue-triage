package uk.gov.dwp.queue.triage.web.server.configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.status.FailedMessageStatusHistoryClient;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import static java.util.Collections.singletonList;

@Profile("!component-test")
@Configuration
@Import(JacksonConfiguration.class)
public class CoreClientConfiguration {

    @Bean
    public SearchFailedMessageClient searchFailedMessageClient(JacksonJsonProvider jacksonJsonProvider,
                                                               ClientProperties clientProperties) {
        return JAXRSClientFactory.create(
                clientProperties.getCore().getUrl(),
                SearchFailedMessageClient.class,
                singletonList(jacksonJsonProvider)
        );
    }

    @Bean
    public LabelFailedMessageClient labelFailedMessageClient(JacksonJsonProvider jacksonJsonProvider,
                                                             ClientProperties clientProperties) {
        return JAXRSClientFactory.create(
                clientProperties.getCore().getUrl(),
                LabelFailedMessageClient.class,
                singletonList(jacksonJsonProvider)
        );
    }

    @Bean
    public DeleteFailedMessageClient deleteFailedMessageClient(JacksonJsonProvider jacksonJsonProvider,
                                                               ClientProperties clientProperties) {
        return JAXRSClientFactory.create(
                clientProperties.getCore().getUrl(),
                DeleteFailedMessageClient.class,
                singletonList(jacksonJsonProvider)
        );
    }

    @Bean
    public ResendFailedMessageClient resendFailedMessageClient(JacksonJsonProvider jacksonJsonProvider,
                                                               ClientProperties clientProperties) {
        return JAXRSClientFactory.create(
                clientProperties.getCore().getUrl(),
                ResendFailedMessageClient.class,
                singletonList(jacksonJsonProvider)
        );
    }

    @Bean
    public FailedMessageStatusHistoryClient failedMessageStatusHistoryClient(JacksonJsonProvider jacksonJsonProvider,
                                                                             ClientProperties clientProperties) {
        return JAXRSClientFactory.create(
                clientProperties.getCore().getUrl(),
                FailedMessageStatusHistoryClient.class,
                singletonList(jacksonJsonProvider)
        );
    }
}
