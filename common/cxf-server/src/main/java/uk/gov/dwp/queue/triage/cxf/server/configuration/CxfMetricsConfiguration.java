package uk.gov.dwp.queue.triage.cxf.server.configuration;

import org.apache.cxf.metrics.MetricsFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ProviderRegistry;
import uk.gov.dwp.queue.triage.metrics.configuration.MetricRegistryConfiguration;

@Configuration
@Import({
        CxfConfiguration.class,
        MetricRegistryConfiguration.class
})
public class CxfMetricsConfiguration {

    @Bean
    @ConditionalOnProperty(name = "cxf.metrics.enabled", havingValue = "true")
    public MetricsFeature metricsFeature(ProviderRegistry providerRegistry) {
        return providerRegistry.add(new MetricsFeature());
    }
}
