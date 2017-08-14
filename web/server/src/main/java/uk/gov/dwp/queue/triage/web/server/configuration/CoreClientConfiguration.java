package uk.gov.dwp.queue.triage.web.server.configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import static java.util.Collections.singletonList;

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
}
