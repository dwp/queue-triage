package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.FAILED;

public class MongoFailedMessageSearchServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private DBCollection mongoCollection;
    @Mock
    private MongoSearchRequestAdapter mongoSearchRequestAdapter;
    @Mock
    private MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder;
    @Mock
    private FailedMessageConverter failedMessageConverter;
    @Mock
    private SearchFailedMessageRequest request;
    @Mock
    private BasicDBObject query;
    @Mock
    private DBCursor dbCursor;
    @Mock
    private FailedMessage failedMessage;

    @InjectMocks
    private MongoFailedMessageSearchService underTest;

    @Test
    public void search() throws Exception {
        when(mongoSearchRequestAdapter.toQuery(request)).thenReturn(query);
        when(mongoCollection.find(query)).thenReturn(dbCursor);
        when(failedMessageConverter.convertToList(dbCursor)).thenReturn(singletonList(failedMessage));

        assertThat(underTest.search(request), contains(failedMessage));

        verify(mongoSearchRequestAdapter).toQuery(request);
        verify(mongoCollection).find(query);
        verify(failedMessageConverter).convertToList(dbCursor);
    }

    @Test
    public void findByStatus() throws Exception {
        when(mongoStatusHistoryQueryBuilder.currentStatusEqualTo(FAILED)).thenReturn(query);
        when(mongoCollection.find(query)).thenReturn(dbCursor);
        when(failedMessageConverter.convertToList(dbCursor)).thenReturn(singletonList(failedMessage));

        assertThat(underTest.findByStatus(FAILED), contains(failedMessage));

        verify(mongoStatusHistoryQueryBuilder).currentStatusEqualTo(FAILED);
        verify(mongoCollection).find(query);
        verify(failedMessageConverter).convertToList(dbCursor);
    }
}