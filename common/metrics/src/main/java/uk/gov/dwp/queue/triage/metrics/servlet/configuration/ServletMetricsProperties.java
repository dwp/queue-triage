package uk.gov.dwp.queue.triage.metrics.servlet.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "server.metrics",
        ignoreUnknownFields = true
)
public class ServletMetricsProperties {

    private boolean enabled;
    private String prefix;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
