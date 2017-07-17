package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.DBObject;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectMatcher.hasField;

public class MongoSearchRequestAdapterTest {

    private final MongoSearchRequestAdapter underTest = new MongoSearchRequestAdapter();

    @Test
    public void searchRequestWithDestination() {
        final DBObject dbObject = underTest.toQuery(newSearchFailedMessageRequest()
                .withBroker("broker-name")
                .withDestination("mars")
                .build()
        );
        assertThat(dbObject, Matchers.allOf(
                hasField("destination.brokerName", "broker-name"),
                hasField("destination.name", "mars")
        ));
    }

    @Test
    public void searchRequestWithoutDestination() throws Exception {
        final DBObject dbObject = underTest.toQuery(newSearchFailedMessageRequest()
                .withBroker("broker-name")
                .build()
        );
        assertThat(dbObject, Matchers.allOf(
                hasField("destination.brokerName", "broker-name")
        ));
    }
}