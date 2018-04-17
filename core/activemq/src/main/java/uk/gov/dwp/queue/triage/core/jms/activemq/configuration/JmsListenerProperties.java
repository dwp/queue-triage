package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "jms.activemq")
public class JmsListenerProperties {

    @Valid
    private List<BrokerProperties> brokers;

    public List<BrokerProperties> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<BrokerProperties> brokers) {
        this.brokers = brokers;
    }

    public Optional<BrokerProperties> getBrokerConfigFor(String brokerName) {
        return brokers.stream().filter(broker -> broker.getName().equals(brokerName)).findFirst();
    }

    public static class BrokerProperties {

        private String name;
        private String url;
        private String queue;
        private ResendProperties resend;
        private boolean readOnly;
        private ReadOnlyProperties read;

        private TlsProperties tls;

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

        public TlsProperties getTls() {
            return tls;
        }

        public void setTls(TlsProperties tls) {
            this.tls = tls;
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

        public static class TlsProperties {

            @NotNull
            private String keyStoreFilePath;
            @NotEmpty
            private char[] keyStorePassword;
            @NotNull
            private String trustStoreFilePath;
            @NotEmpty
            private char[] trustStorePassword;

            public String getKeyStoreFilePath() {
                return keyStoreFilePath;
            }

            public void setKeyStoreFilePath(String keyStoreFilePath) {
                this.keyStoreFilePath = keyStoreFilePath;
            }

            public char[] getKeyStorePassword() {
                return keyStorePassword;
            }

            public void setKeyStorePassword(char[] keyStorePassword) {
                this.keyStorePassword = keyStorePassword;
            }

            public String getTrustStoreFilePath() {
                return trustStoreFilePath;
            }

            public void setTrustStoreFilePath(String trustStoreFilePath) {
                this.trustStoreFilePath = trustStoreFilePath;
            }

            public char[] getTrustStorePassword() {
                return trustStorePassword;
            }

            public void setTrustStorePassword(char[] trustStorePassword) {
                this.trustStorePassword = trustStorePassword;
            }

        }
    }


}
