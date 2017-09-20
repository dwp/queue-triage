package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import java.util.Optional;

public class DestinationDBObjectConverter implements DBObjectConverter<Destination> {

    public static final String BROKER_NAME = "brokerName";
    public static final String NAME = "name";

    @Override
    public Destination convertToObject(DBObject dbObject) {
        return new Destination((String)dbObject.get(BROKER_NAME), Optional.ofNullable((String)dbObject.get(NAME)));
    }

    @Override
    public DBObject convertFromObject(Destination item) {
        return new BasicDBObject()
                .append(BROKER_NAME, item.getBrokerName())
                .append(NAME, item.getName().orElse(null));
    }
}