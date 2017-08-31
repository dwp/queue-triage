package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.DBObject;
import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;

import static com.mongodb.QueryOperators.IN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectMatcher.hasField;

public class MongoSearchRequestAdapterTest {

    private final MongoSearchRequestAdapter underTest = new MongoSearchRequestAdapter();

    @Test
    public void searchRequestWithAllParametersSpecified() {
        final DBObject dbObject = underTest.toQuery(newSearchFailedMessageRequest()
                .withBroker("broker-name")
                .withDestination("mars")
                .withStatus(FailedMessageStatus.FAILED)
                .withStatus(FailedMessageStatus.RESENDING)
                .withStatus(FailedMessageStatus.SENT)
                .build()
        );
        assertThat(dbObject, Matchers.allOf(
                hasField("destination.brokerName", equalTo("broker-name")),
                hasField("statusHistory.0.status", hasField(IN, hasItems("FAILED", "CLASSIFIED", "RESEND", "SENT"))),
                hasField("destination.name", equalTo("mars"))
        ));
    }

    @Test
    public void searchRequestWithoutDestinationAndBrokerAndDefaultStatus() throws Exception {
        final DBObject dbObject = underTest.toQuery(newSearchFailedMessageRequest().build());

        assertThat(dbObject, Matchers.allOf(
                hasField("statusHistory.0.status", hasField("$ne", equalTo("DELETED")))
        ));
    }
}