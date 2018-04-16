package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MongoFailedMessageSearchService implements FailedMessageSearchService {

    private static final Logger LOGGER = getLogger(MongoFailedMessageSearchService.class);

    private final MongoCollection<Document> dbCollection;
    private final MongoSearchRequestAdapter mongoSearchRequestAdapter;
    private final FailedMessageConverter failedMessageConverter;
    private final MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder;

    public MongoFailedMessageSearchService(MongoCollection<Document> dbCollection,
                                           MongoSearchRequestAdapter mongoSearchRequestAdapter,
                                           FailedMessageConverter failedMessageConverter,
                                           MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder) {
        this.dbCollection = dbCollection;
        this.mongoSearchRequestAdapter = mongoSearchRequestAdapter;
        this.failedMessageConverter = failedMessageConverter;
        this.mongoStatusHistoryQueryBuilder = mongoStatusHistoryQueryBuilder;
    }

    @Override
    public Collection<FailedMessage> search(SearchFailedMessageRequest request) {
        final List<FailedMessage> failedMessages = getFailedMessages(mongoSearchRequestAdapter.toQuery(request));
        LOGGER.debug("Found {} failedMessages", failedMessages.size());
        return failedMessages;
    }

    @Override
    public Collection<FailedMessage> findByStatus(StatusHistoryEvent.Status status) {
        final List<FailedMessage> failedMessages = getFailedMessages(mongoStatusHistoryQueryBuilder.currentStatusEqualTo(status));
        LOGGER.debug("Found {} failedMessages with status {}", failedMessages.size(), status);
        return failedMessages;
    }

    private List<FailedMessage> getFailedMessages(Document document) {
        return dbCollection
                .find(document)
                .map(failedMessageConverter::convertToObject)
                .into(new ArrayList<>());
    }
}