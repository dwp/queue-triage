package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.Operator.AND;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.CONTENT;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.JMS_MESSAGE_ID;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter.fromFailedMessageStatus;

public class MongoSearchRequestAdapter {

    private final MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder;

    public MongoSearchRequestAdapter() {
        mongoStatusHistoryQueryBuilder = new MongoStatusHistoryQueryBuilder();
    }

    public DBObject toQuery(SearchFailedMessageRequest request) {
        BasicDBObject query;
        if (request.getStatuses().size() > 0) {
            query = mongoStatusHistoryQueryBuilder.currentStatusIn(fromFailedMessageStatus(request.getStatuses()));
        } else {
            query = mongoStatusHistoryQueryBuilder.currentStatusNotEqualTo(DELETED);
        }
        List<BasicDBObject> predicates = new ArrayList<>();

        request.getBroker().ifPresent(broker -> predicates.add(new BasicDBObject(DESTINATION + "." + BROKER_NAME, broker)));
        request.getDestination().ifPresent(destination -> predicates.add(new BasicDBObject(DESTINATION + "." + NAME, destination)));
        request.getContent().ifPresent(content -> predicates.add(new BasicDBObject(CONTENT, Pattern.compile(content))));
        request.getJmsMessageId().ifPresent(jmsMessageId -> predicates.add(new BasicDBObject(JMS_MESSAGE_ID, jmsMessageId)));
        if (predicates.size() > 0) {
            query.append(request.getOperator() == AND ? QueryOperators.AND : QueryOperators.OR, predicates);
        }
        return query;
    }
}
