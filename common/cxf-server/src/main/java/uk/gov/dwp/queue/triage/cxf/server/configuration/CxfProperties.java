package uk.gov.dwp.queue.triage.cxf.server.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "cxf",
        ignoreUnknownFields = true
)
public class CxfProperties {

    private String contextPath;
    private CxfMetricsProperties metrics = new CxfMetricsProperties();

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public CxfMetricsProperties getMetrics() {
        return metrics;
    }

    public void setMetrics(CxfMetricsProperties metrics) {
        this.metrics = metrics;
    }

    public static class CxfMetricsProperties {
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

}
