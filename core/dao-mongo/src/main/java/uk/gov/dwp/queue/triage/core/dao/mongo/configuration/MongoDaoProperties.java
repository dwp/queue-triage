package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
@ConfigurationProperties(prefix = "dao.mongo")
public class MongoDaoProperties {

    private Optional<String> uri = Optional.empty();
    private Optional<String> host = Optional.empty();
    private Optional<Integer> port = Optional.empty();
    private Optional<String> dbName = Optional.empty();
    private Collection failedMessage = new Collection();
    private MongoOptions options = new MongoOptions();

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

    public MongoClient createClient() {
        return uri
                .map(s -> new MongoClient(new MongoClientURI(s)))
                .orElseGet(() -> new MongoClient(getHost(), getPort()));
    }

    public MongoOptions getOptions() {
        return options;
    }

    public void setOptions(MongoOptions options) {
        this.options = options;
    }

    public MongoClientOptions mongoClientOptions() {
        return new MongoClientOptions.Builder()
                .sslEnabled(options.ssl.enabled)
                .sslInvalidHostNameAllowed(options.ssl.invalidHostnameAllowed)
                .build();
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

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public void setInvalidHostnameAllowed(boolean invalidHostnameAllowed) {
                this.invalidHostnameAllowed = invalidHostnameAllowed;
            }
        }
    }
}
