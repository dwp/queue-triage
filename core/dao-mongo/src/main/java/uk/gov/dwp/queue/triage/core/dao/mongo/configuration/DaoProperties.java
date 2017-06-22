package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "queue.triage.db")
public class DaoProperties {

    public static class Collection {

        private String failedMessage;

        public String getFailedMessage() {
            return failedMessage;
        }

        public void setFailedMessage(String failedMessage) {
            this.failedMessage = failedMessage;
        }
    }

    private String dbName;
    private Collection collection;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
}
