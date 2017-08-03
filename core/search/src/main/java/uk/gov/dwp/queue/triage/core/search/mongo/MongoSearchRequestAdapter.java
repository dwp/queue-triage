package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;

import static com.mongodb.QueryOperators.IN;
import static java.util.stream.Collectors.toList;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDBObjectConverter.STATUS;

public class MongoSearchRequestAdapter {

    public DBObject toQuery(SearchFailedMessageRequest request) {
        BasicDBObject query = new BasicDBObject()
                .append(DESTINATION + "." + BROKER_NAME, request.getBroker())
                .append(STATUS_HISTORY + ".0." + STATUS, statusList(request));
        request.getDestination().ifPresent(destination -> query.append(DESTINATION + "." + NAME, destination));
        return query;
    }

    private BasicDBObject statusList(SearchFailedMessageRequest request) {
        return new BasicDBObject(IN, request.getStatuses().stream().map(Enum::name).collect(toList()));
    }
}
