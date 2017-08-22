package uk.gov.dwp.migration.mongo.demo.cxf.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfiguration {

    @Bean
    public ProviderRegistry providerRegistry() {
        return new ProviderRegistry();
    }

    @Bean
    public ResourceRegistry resourceRegistry() {
        return new ResourceRegistry();
    }
}
