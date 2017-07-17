package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MongoFailedMessageSearchServiceTest {

    @Mock
    private DBCollection mongoCollection;
    @Mock
    private MongoSearchRequestAdapter mongoSearchRequestAdapter;
    @Mock
    private MongoSearchResponseAdapter mongoSearchResponseAdapter;
    @Mock
    private SearchFailedMessageRequest request;
    @Mock
    private DBObject query;
    @Mock
    private DBCursor dbCursor;
    @Mock
    private BasicDBObject dbObject;
    @Mock
    private SearchFailedMessageResponse response;

    @InjectMocks
    private MongoFailedMessageSearchService underTest;

    @Test
    public void name() throws Exception {
        when(mongoSearchRequestAdapter.toQuery(request)).thenReturn(query);
        when(mongoCollection.find(query)).thenReturn(dbCursor);
        when(dbCursor.iterator()).thenReturn(Arrays.asList((DBObject)dbObject).iterator());
        when(mongoSearchResponseAdapter.toResponse(dbObject)).thenReturn(response);

        assertThat(underTest.search(request), contains(response));

        verify(mongoSearchRequestAdapter).toQuery(request);
        verify(mongoCollection).find(query);
    }
}