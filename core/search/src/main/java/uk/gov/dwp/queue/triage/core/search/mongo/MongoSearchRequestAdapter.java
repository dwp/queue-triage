package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.QueryOperators;
import org.bson.Document;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.Operator.AND;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDocumentConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDocumentConverter.NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.CONTENT;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.JMS_MESSAGE_ID;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter.fromFailedMessageStatus;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;

public class MongoSearchRequestAdapter {

    private final MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder;

    public MongoSearchRequestAdapter() {
        mongoStatusHistoryQueryBuilder = new MongoStatusHistoryQueryBuilder();
    }

    public Document toQuery(SearchFailedMessageRequest request) {
        Document query;
        if (request.getStatuses().size() > 0) {
            query = mongoStatusHistoryQueryBuilder.currentStatusIn(fromFailedMessageStatus(request.getStatuses()));
        } else {
            query = mongoStatusHistoryQueryBuilder.currentStatusNotEqualTo(DELETED);
        }
        List<Document> predicates = new ArrayList<>();

        request.getBroker().ifPresent(broker -> predicates.add(new Document(DESTINATION + "." + BROKER_NAME, broker)));
        request.getDestination().ifPresent(destination -> predicates.add(new Document(DESTINATION + "." + NAME, destination)));
        request.getContent().ifPresent(content -> predicates.add(new Document(CONTENT, Pattern.compile(content))));
        request.getJmsMessageId().ifPresent(jmsMessageId -> predicates.add(new Document(JMS_MESSAGE_ID, jmsMessageId)));
        if (predicates.size() > 0) {
            query.append(request.getOperator() == AND ? QueryOperators.AND : QueryOperators.OR, predicates);
        }
        return query;
    }
}
