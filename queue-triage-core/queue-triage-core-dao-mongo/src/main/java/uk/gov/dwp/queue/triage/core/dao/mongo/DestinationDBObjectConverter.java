package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.domain.Destination;

public class DestinationDBObjectConverter implements DBObjectConverter<Destination> {

    static final String BROKER_NAME = "brokerName";
    static final String NAME = "name";

    @Override
    public Destination convertToObject(DBObject dbObject) {
        return new Destination((String)dbObject.get(BROKER_NAME), (String)dbObject.get(NAME));
    }

    @Override
    public DBObject convertFromObject(Destination item) {
        return new BasicDBObject()
                .append(BROKER_NAME, item.getBrokerName())
                .append(NAME, item.getName());
    }
}