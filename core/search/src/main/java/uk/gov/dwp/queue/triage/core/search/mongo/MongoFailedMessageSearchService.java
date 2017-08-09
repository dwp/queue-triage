package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MongoFailedMessageSearchService implements FailedMessageSearchService {

    private static final Logger LOGGER = getLogger(MongoFailedMessageSearchService.class);

    private final DBCollection dbCollection;
    private final MongoSearchRequestAdapter mongoSearchRequestAdapter;
    private final FailedMessageConverter failedMessageConverter;

    public MongoFailedMessageSearchService(DBCollection dbCollection,
                                           MongoSearchRequestAdapter mongoSearchRequestAdapter,
                                           FailedMessageConverter failedMessageConverter) {
        this.dbCollection = dbCollection;
        this.mongoSearchRequestAdapter = mongoSearchRequestAdapter;
        this.failedMessageConverter = failedMessageConverter;
    }

    @Override
    public Collection<FailedMessage> search(SearchFailedMessageRequest request) {
        // TODO: Consider adding FailedMessageConverter#convertToObjects(DBCursor dbCursor, Class<T extends Collection> collection)
        DBCursor dbCursor = dbCollection.find(mongoSearchRequestAdapter.toQuery(request));
        List<FailedMessage> responses = new ArrayList<>();
        for (DBObject dbObject : dbCursor) {
            responses.add(failedMessageConverter.convertToObject(dbObject));
        }
        dbCursor.close();
        LOGGER.debug("Found {} results", responses.size());
        return responses;
    }
}