package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MongoFailedMessageSearchService implements FailedMessageSearchService {

    private final DBCollection dbCollection;
    private final MongoSearchRequestAdapter mongoSearchRequestAdapter;
    private final MongoSearchResponseAdapter mongoSearchResponseAdapter;

    public MongoFailedMessageSearchService(DBCollection dbCollection,
                                           MongoSearchRequestAdapter mongoSearchRequestAdapter,
                                           MongoSearchResponseAdapter mongoSearchResponseAdapter) {
        this.dbCollection = dbCollection;
        this.mongoSearchRequestAdapter = mongoSearchRequestAdapter;
        this.mongoSearchResponseAdapter = mongoSearchResponseAdapter;
    }

    @Override
    public Collection<SearchFailedMessageResponse> search(SearchFailedMessageRequest request) {
        DBCursor dbCursor = dbCollection.find(mongoSearchRequestAdapter.toQuery(request));
        List<SearchFailedMessageResponse> responses = new ArrayList<>();
        for (DBObject dbObject : dbCursor) {
            responses.add(mongoSearchResponseAdapter.toResponse((BasicDBObject)dbObject));
        }
        return responses;
    }
}