package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;

import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;

public class MongoSearchRequestAdapter {

    public DBObject toQuery(SearchFailedMessageRequest request) {
        BasicDBObject query = new BasicDBObject()
                .append(DESTINATION + "." + BROKER_NAME, request.getBroker());
        request.getDestination().ifPresent(destination -> query.append(DESTINATION + "." + NAME, destination));
        return query;
    }
}
