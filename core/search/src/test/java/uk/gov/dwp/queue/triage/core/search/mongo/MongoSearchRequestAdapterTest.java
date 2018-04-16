package uk.gov.dwp.queue.triage.core.search.mongo;

import org.bson.Document;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;

import java.util.regex.Pattern;

import static com.mongodb.QueryOperators.IN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAllCriteria;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.searchMatchingAnyCriteria;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DocumentMatcher.hasField;

public class MongoSearchRequestAdapterTest {

    private final MongoSearchRequestAdapter underTest = new MongoSearchRequestAdapter();

    @Test
    public void searchRequestMatchingAllCriteriaWithAllParametersSpecified() {
        final Document document = underTest.toQuery(searchMatchingAllCriteria()
                .withBroker("broker-name")
                .withDestination("mars")
                .withStatus(FailedMessageStatus.FAILED)
                .withStatus(FailedMessageStatus.RESENDING)
                .withStatus(FailedMessageStatus.SENT)
                .withContent("id")
                .withJmsMessageId("ID:localhost.localdomain-46765-1518703251379-5:1:1:1:1")
                .build()
        );
        assertThat(document, allOf(
                hasField("statusHistory.0.status", hasField(IN, hasItems("FAILED", "CLASSIFIED", "RESEND", "SENT"))),
                hasField("$and", containsInAnyOrder(
                    hasField("destination.brokerName", equalTo("broker-name")),
                    hasField("destination.name", equalTo("mars")),
                    hasField("content", allOf(willMatch("user_id"), willMatch("id"), willMatch("hidden"), not(willMatch("find")))),
                    hasField("jmsMessageId", equalTo("ID:localhost.localdomain-46765-1518703251379-5:1:1:1:1"))
        ))));
    }

    @Test
    public void searchRequestMatchingAnyCriteriaWithAllParametersSpecified() {
        final Document document = underTest.toQuery(searchMatchingAnyCriteria()
                .withBroker("broker-name")
                .withDestination("mars")
                .withStatus(FailedMessageStatus.FAILED)
                .withStatus(FailedMessageStatus.RESENDING)
                .withStatus(FailedMessageStatus.SENT)
                .withContent("id")
                .withJmsMessageId("ID:localhost.localdomain-46765-1518703251379-5:1:1:1:1")
                .build()
        );
        assertThat(document, allOf(
                hasField("statusHistory.0.status", hasField(IN, hasItems("FAILED", "CLASSIFIED", "RESEND", "SENT"))),
                hasField("$or", containsInAnyOrder(
                    hasField("destination.brokerName", equalTo("broker-name")),
                    hasField("destination.name", equalTo("mars")),
                    hasField("content", allOf(willMatch("user_id"), willMatch("id"), willMatch("hidden"), not(willMatch("find")))),
                    hasField("jmsMessageId", equalTo("ID:localhost.localdomain-46765-1518703251379-5:1:1:1:1"))
        ))));
    }

    private TypeSafeMatcher<Pattern> willMatch(final String input) {
        return new TypeSafeMatcher<Pattern>() {
            @Override
            protected boolean matchesSafely(Pattern pattern) {
                return pattern.matcher(input).find();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected the pattern to match ").appendText(input);
            }
        };
    }

    @Test
    public void searchRequestWithoutDestinationAndBrokerAndDefaultStatus() {
        final Document document = underTest.toQuery(searchMatchingAllCriteria().build());

        assertThat(document, Matchers.allOf(
                hasField("statusHistory.0.status", hasField("$ne", equalTo("DELETED")))
        ));
    }
}