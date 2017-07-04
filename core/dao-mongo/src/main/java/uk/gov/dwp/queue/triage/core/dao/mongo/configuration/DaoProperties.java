package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import com.mongodb.ServerAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
@ConfigurationProperties(prefix = "dao.mongo")
public class DaoProperties {

    private Optional<String> host;
    private Optional<Integer> port;
    private Optional<String> dbName;
    private Collection failedMessage;

    public String getHost() {
        return host.orElse(ServerAddress.defaultHost());
    }

    public void setHost(String host) {
        this.host = ofNullable(host);
    }

    public Integer getPort() {
        return port.orElse(ServerAddress.defaultPort());
    }

    public void setPort(Integer port) {
        this.port = ofNullable(port);
    }

    public String getDbName() {
        return dbName.orElse("queue-triage");
    }

    public void setDbName(String dbName) { this.dbName = ofNullable(dbName);
    }

    public Collection getFailedMessage() {
        return failedMessage;
    }

    public void setFailedMessage(Collection failedMessage) {
        this.failedMessage = failedMessage;
    }

    public static class Collection {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
