package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import com.mongodb.MongoClientOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@ConfigurationProperties(prefix = "dao.mongo")
public class MongoDaoProperties {

    private List<MongoServerAddress> serverAddresses = new ArrayList<>();
    private String dbName;
    private Collection failedMessage = new Collection();
    private MongoOptions options = new MongoOptions();

    public List<MongoServerAddress> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(List<MongoServerAddress> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public String getDbName() {
        return Optional.ofNullable(dbName).orElse("queue-triage");
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
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

    public MongoClientOptions.Builder mongoClientOptions() {
        return new MongoClientOptions.Builder()
            .sslEnabled(options.ssl.enabled)
            .sslInvalidHostNameAllowed(options.ssl.invalidHostnameAllowed);
    }

    public static class Collection {

        private String name = "failedMessage";
        private String username;
        private char[] password;
        private boolean audited;
        private AuditCollection auditCollection = new AuditCollection();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Optional<String> getOptionalUsername() {
            return ofNullable(username);
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Optional<char[]> getOptionalPassword() {
            return ofNullable(password);
        }

        public char[] getPassword() {
            return password;
        }

        public void setPassword(char[] password) {
            this.password = password;
        }

        public boolean isAudited() {
            return audited;
        }

        public void setAudited(boolean audited) {
            this.audited = audited;
        }

        public AuditCollection getAuditCollection() {
            return auditCollection;
        }

        public void setAuditCollection(AuditCollection auditCollection) {
            this.auditCollection = auditCollection;
        }

        public static class AuditCollection {
            private String name;

            public String getName() {
                return Optional.ofNullable(name).orElse("failedMessageAudit");
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    public static class MongoServerAddress {

        private String host;
        private Integer port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
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
