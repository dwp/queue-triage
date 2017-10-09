package uk.gov.dwp.queue.triage.metrics.configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricRegistryConfiguration {

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public JmxReporter jmxReporter(MetricRegistry metricRegistry) {
        return JmxReporter.forRegistry(metricRegistry).build();
    }


}
