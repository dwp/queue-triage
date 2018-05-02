package uk.gov.dwp.queue.triage.core.search.mongo;

import org.bson.Document;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
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
    private final Document document = mock(Document.class);

    private final MongoSearchResponseAdapter underTest = new MongoSearchResponseAdapter(failedMessageConverter);

    @Test
    public void convertBasicDBbjectToSearchResponse() {
        when(failedMessageConverter.getDestination(document)).thenReturn(new Destination("broker-name", Optional.of("queue-name")));
        when(failedMessageConverter.getStatusHistoryEvent(document)).thenReturn(new StatusHistoryEvent(StatusHistoryEvent.Status.FAILED, NOW));
        when(failedMessageConverter.getFailedMessageId(document)).thenReturn(FAILED_MESSAGE_ID);
        when(failedMessageConverter.getContent(document)).thenReturn("some-content");

        SearchFailedMessageResponse response = underTest.toResponse(document);

        assertThat(response, new TypeSafeMatcher<SearchFailedMessageResponse>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(SearchFailedMessageResponse response) {
                return FAILED_MESSAGE_ID.equals(response.getFailedMessageId()) &&
                        "some-content".equals(response.getContent()) &&
                        NOW.equals(response.getStatusDateTime()) &&
                        FailedMessageStatus.FAILED.equals(response.getStatus()) &&
                        "broker-name".equals(response.getBroker()) &&
                        Optional.of("queue-name").equals(response.getDestination());
            }
        });

    }
}