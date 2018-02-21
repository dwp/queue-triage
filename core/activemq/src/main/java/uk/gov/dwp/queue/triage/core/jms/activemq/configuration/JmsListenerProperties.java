package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "jms.activemq")
public class JmsListenerProperties {

    private List<BrokerProperties> brokers;

    public List<BrokerProperties> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<BrokerProperties> brokers) {
        this.brokers = brokers;
    }

    public static class BrokerProperties {
        private String name;
        private String url;
        private String queue;
        private ResendProperties resend;
        private boolean readOnly;
        private ReadOnlyProperties read;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public ResendProperties getResend() {
            return resend;
        }

        public void setResend(ResendProperties resend) {
            this.resend = resend;
        }

        public static class ResendProperties {
            private long frequency;

            public long getFrequency() {
                return frequency;
            }

            public void setFrequency(long frequency) {
                this.frequency = frequency;
            }
        }

        public static class ReadOnlyProperties {
            // TODO: Consider sensible defaults for this property? 500ms??
            private long frequency;

            public long getFrequency() {
                return frequency;
            }

            public void setFrequency(long frequency) {
                this.frequency = frequency;
            }
        }
    }
}
