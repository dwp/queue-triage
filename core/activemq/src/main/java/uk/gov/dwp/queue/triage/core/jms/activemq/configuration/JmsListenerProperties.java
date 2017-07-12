package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "jms.activemq")
public class JmsListenerProperties {

    private BeanFactory beanFactory;
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
    }
}
