package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import com.mongodb.ServerAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
@ConfigurationProperties(prefix = "dao.mongo")
public class MongoDaoProperties {

    private List<MongoServerAddress> serverAddresses = new ArrayList<>();
    private Optional<String> dbName = Optional.empty();
    private Collection failedMessage = new Collection();
    private MongoOptions options = new MongoOptions();

    public List<MongoServerAddress> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(List<MongoServerAddress> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public String getDbName() {
        return dbName.orElse("queue-triage");
    }

    public void setDbName(String dbName) {
        this.dbName = ofNullable(dbName);
    }

    public Collection getFailedMessage() {
        return failedMessage;
    }

    public void setFailedMessage(Collection failedMessage) {
        this.failedMessage = failedMessage;
    }

    public MongoOptions getOptions() {
        return options;
    }

    public void setOptions(MongoOptions options) {
        this.options = options;
    }

    public static class Collection {

        private String name = "failedMessage";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MongoServerAddress {
        private String host;
        private Integer port;

        public String getHost() {
            return ofNullable(host).orElse(ServerAddress.defaultHost());
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return ofNullable(port).orElse(ServerAddress.defaultPort());
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    public static class MongoOptions {

        private SSL ssl = new SSL();

        public SSL getSsl() {
            return ssl;
        }

        public void setSsl(SSL ssl) {
            this.ssl = ssl;
        }

        public static class SSL {
            private boolean enabled;
            private boolean invalidHostnameAllowed;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public boolean isInvalidHostnameAllowed() {
                return invalidHostnameAllowed;
            }

            public void setInvalidHostnameAllowed(boolean invalidHostnameAllowed) {
                this.invalidHostnameAllowed = invalidHostnameAllowed;
            }
        }
    }
}
