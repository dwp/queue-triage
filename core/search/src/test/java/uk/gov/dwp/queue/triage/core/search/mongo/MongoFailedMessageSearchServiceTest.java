package uk.gov.dwp.queue.triage.core.search.mongo;

import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.dao.mongo.AbstractMongoDaoTest;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageMongoDao;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static java.time.Instant.now;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class MongoFailedMessageSearchServiceTest extends AbstractMongoDaoTest {

    private static final String JMS_MESSAGE_ID = "jms-message-id";

    private final FailedMessageId failedMessageId = newFailedMessageId();
    private final FailedMessageBuilder failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
            .withFailedMessageId(failedMessageId)
            .withJmsMessageId(JMS_MESSAGE_ID)
            .withDestination(new Destination("broker", of("queue.name")))
            .withContent("Hello")
            .withSentDateTime(now())
            .withFailedDateTime(now());


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MongoSearchRequestAdapter mongoSearchRequestAdapter;
    @Mock
    private MongoStatusHistoryQueryBuilder mongoStatusHistoryQueryBuilder;
    @Mock
    private SearchFailedMessageRequest request;
    @Autowired
    private FailedMessageConverter failedMessageConverter;
    @Autowired
    private FailedMessageMongoDao failedMessageMongoDao;

    private MongoFailedMessageSearchService underTest;

    @Before
    public void setUp() {
        super.setUp();
        underTest = new MongoFailedMessageSearchService(collection, mongoSearchRequestAdapter, failedMessageConverter, mongoStatusHistoryQueryBuilder, failedMessageMongoDao);
    }

    @Override
    protected String getCollectionName() {
        return "failedMessage";
    }

    @Test
    public void search() {
        failedMessageMongoDao.insert(failedMessageBuilder.build());
        when(mongoSearchRequestAdapter.toQuery(request)).thenReturn(new Document());

        assertThat(underTest.search(request), contains(aFailedMessage().withFailedMessageId(equalTo(failedMessageId))));
    }

    @Test
    public void findByStatus() {
        failedMessageMongoDao.insert(failedMessageBuilder.build());
        when(mongoStatusHistoryQueryBuilder.currentStatusEqualTo(FAILED)).thenReturn(new Document());

        assertThat(underTest.findByStatus(FAILED), contains(aFailedMessage().withFailedMessageId(equalTo(failedMessageId))));
    }
}