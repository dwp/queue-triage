package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;

import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter.fromFailedMessageStatus;

public class MongoSearchRequestAdapter {

    private final MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder;

    public MongoSearchRequestAdapter() {
        mongoStatusHistoryQueryBuilder = new MongoStatusHistoryQueryBuilder();
    }

    public DBObject toQuery(SearchFailedMessageRequest request) {
        BasicDBObject query = new BasicDBObject();
        request.getBroker().ifPresent(broker -> query.append(DESTINATION + "." + BROKER_NAME, broker));
        request.getDestination().ifPresent(destination -> query.append(DESTINATION + "." + NAME, destination));
        if (request.getStatuses().size() > 0) {
            mongoStatusHistoryQueryBuilder.currentStatusIn(
                    query,
                    fromFailedMessageStatus(request.getStatuses())
            );
        }
        return query;
    }
}
