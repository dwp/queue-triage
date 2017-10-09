package uk.gov.dwp.queue.triage.metrics.servlet.configuration;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.metrics.configuration.MetricRegistryConfiguration;

@Configuration
@Import({
        MetricRegistryConfiguration.class
})
@EnableConfigurationProperties(ServletMetricsProperties.class)
public class ServletMetricsConfiguration {

    @Bean
    @ConditionalOnProperty(name = "server.metrics.enabled", havingValue = "true")
    public FilterRegistrationBean filterRegistrationBean(ServletMetricsProperties servletMetricsProperties) {
        FilterRegistrationBean filterRegistrationBean =  new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new InstrumentedFilter());
        filterRegistrationBean.addInitParameter("name-prefix", servletMetricsProperties.getPrefix());
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnProperty(name = "server.metrics.enabled", havingValue = "true")
    public InstrumentedFilterContextListener instrumentedFilterContextListener(MetricRegistry metricRegistry) {
        return new InstrumentedFilterContextListener() {
            @Override
            protected MetricRegistry getMetricRegistry() {
                return metricRegistry;
            }
        };
    }

}
