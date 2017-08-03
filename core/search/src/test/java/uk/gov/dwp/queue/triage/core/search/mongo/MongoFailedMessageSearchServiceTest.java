package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MongoFailedMessageSearchServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private DBCollection mongoCollection;
    @Mock
    private MongoSearchRequestAdapter mongoSearchRequestAdapter;
    @Mock
    private FailedMessageConverter failedMessageConverter;
    @Mock
    private SearchFailedMessageRequest request;
    @Mock
    private DBObject query;
    @Mock
    private DBCursor dbCursor;
    @Mock
    private BasicDBObject dbObject;
    @Mock
    private FailedMessage failedMessage;

    @InjectMocks
    private MongoFailedMessageSearchService underTest;

    @Test
    public void name() throws Exception {
        when(mongoSearchRequestAdapter.toQuery(request)).thenReturn(query);
        when(mongoCollection.find(query)).thenReturn(dbCursor);
        when(dbCursor.iterator()).thenReturn(Arrays.asList((DBObject)dbObject).iterator());
        when(failedMessageConverter.convertToObject(dbObject)).thenReturn(failedMessage);

        assertThat(underTest.search(request), contains(failedMessage));

        verify(mongoSearchRequestAdapter).toQuery(request);
        verify(mongoCollection).find(query);
    }
}