package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.DBCollection;
import org.slf4j.Logger;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MongoFailedMessageSearchService implements FailedMessageSearchService {

    private static final Logger LOGGER = getLogger(MongoFailedMessageSearchService.class);

    private final DBCollection dbCollection;
    private final MongoSearchRequestAdapter mongoSearchRequestAdapter;
    private final FailedMessageConverter failedMessageConverter;
    private final MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder;

    public MongoFailedMessageSearchService(DBCollection dbCollection,
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
        List<FailedMessage> responses = failedMessageConverter
                .convertToList(dbCollection.find(mongoSearchRequestAdapter.toQuery(request)));
        LOGGER.debug("Found {} results", responses.size());
        return responses;
    }

    @Override
    public Collection<FailedMessage> findByStatus(StatusHistoryEvent.Status status) {
        List<FailedMessage> responses = failedMessageConverter
                .convertToList(dbCollection.find(mongoStatusHistoryQueryBuilder.currentStatusEqualTo(status)));
        LOGGER.debug("Found {} results", responses.size());
        return responses;
    }
}