package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MongoSearchResponseAdapterTest {

    private static final Instant NOW = Instant.now();
    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    private final FailedMessageConverter failedMessageConverter = mock(FailedMessageConverter.class);
    private final BasicDBObject basicDBObject = mock(BasicDBObject.class);

    private final MongoSearchResponseAdapter underTest = new MongoSearchResponseAdapter(failedMessageConverter);

    @Test
    public void convertBasicDBbjectToSearchResponse() throws Exception {
        when(failedMessageConverter.getDestination(basicDBObject)).thenReturn(
                new Destination("broker-name", Optional.of("queue-name")));
        when(failedMessageConverter.getFailedMessageId(basicDBObject)).thenReturn(FAILED_MESSAGE_ID);
        when(failedMessageConverter.getContent(basicDBObject)).thenReturn("some-content");
        when(failedMessageConverter.getFailedDateTime(basicDBObject)).thenReturn(NOW);
        when(failedMessageConverter.getSentDateTime(basicDBObject)).thenReturn(NOW);

        SearchFailedMessageResponse response = underTest.toResponse(basicDBObject);

        assertThat(response, new TypeSafeMatcher<SearchFailedMessageResponse>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(SearchFailedMessageResponse response) {
                return FAILED_MESSAGE_ID.equals(response.getFailedMessageId()) &&
                        "some-content".equals(response.getContent()) &&
                        NOW.equals(response.getLastFailedDateTime()) &&
                        NOW.equals(response.getSentDateTime()) &&
                        "broker-name".equals(response.getBroker()) &&
                        Optional.of("queue-name").equals(response.getDestination());
            }
        });

    }
}