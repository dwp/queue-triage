package uk.gov.dwp.queue.triage.core.update;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.CoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.client.update.ContentUpdateRequest;
import uk.gov.dwp.queue.triage.core.client.update.DestinationUpdateRequest;
import uk.gov.dwp.queue.triage.core.client.update.PropertiesUpdateRequest;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.client.update.FailedMessageUpdateRequest.aNewFailedMessageUpdateRequest;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class UpdateFailedMessageComponentTest
        extends CoreComponentTestBase<FailedMessageResourceStage, UpdateFailedMessageWhenStage, SearchFailedMessageThenStage> {

    private final FailedMessageId failedMessageId = newFailedMessageId();

    @Test
    public void submitAnUpdateRequestWithNoData() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withContent("Hello World")
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withProperty("foo", "bar"));

        when().failedMessage$HasTheFollowingUpdates$Submitted(failedMessageId, null);

        then().aFailedMessageWithId$Has(failedMessageId, FailedMessageResponseMatcher.aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello World"))
                .withBroker(equalTo("some-broker"))
                .withDestination(equalTo(Optional.of("some-queue")))
                .withProperties(hasEntry("foo", "bar")));
    }

    @Test
    public void updateTheContentAndDestinationOfAFailedMessage() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withContent("Hello World")
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withProperty("foo", "bar"));

        when().failedMessage$HasTheFollowingUpdates$Submitted(failedMessageId, aNewFailedMessageUpdateRequest()
                .withUpdateRequest(new ContentUpdateRequest("Goodbye Universe"))
                .withUpdateRequest(new DestinationUpdateRequest("another-broker", "another-queue"))
                .build()
        );

        then().aFailedMessageWithId$Has(failedMessageId, FailedMessageResponseMatcher.aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Goodbye Universe"))
                .withBroker(equalTo("another-broker"))
                .withDestination(equalTo(Optional.of("another-queue")))
                .withProperties(hasEntry("foo", "bar")));
    }

    @Test
    public void removePropertiesFromAFailedMessage() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withContent("Hello World")
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withProperty("foo", "bar")
                .withProperty("count", 5)
                .withProperty("ham", "eggs")
        );

        when().failedMessage$HasTheFollowingUpdates$Submitted(failedMessageId, aNewFailedMessageUpdateRequest()
                .withUpdateRequest(new PropertiesUpdateRequest(ImmutableSet.of("foo", "count"), emptyMap()))
                .build()
        );

        then().aFailedMessageWithId$Has(failedMessageId, FailedMessageResponseMatcher.aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello World"))
                .withBroker(equalTo("some-broker"))
                .withDestination(equalTo(Optional.of("some-queue")))
                .withProperties(allOf(hasEntry("ham", "eggs"), not(hasKey("foo")), not(hasKey("count")))));
    }

    @Test
    public void addAndUpdatePropertiesFromAFailedMessage() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId)
                .withContent("Hello World")
                .withBrokerName("some-broker")
                .withDestinationName("some-queue")
                .withProperty("foo", "bar")
                .withProperty("count", 5)
        );

        when().failedMessage$HasTheFollowingUpdates$Submitted(failedMessageId, aNewFailedMessageUpdateRequest()
                .withUpdateRequest(new PropertiesUpdateRequest(emptySet(), ImmutableMap.of("count", 6, "ham", "eggs")))
                .build()
        );

        then().aFailedMessageWithId$Has(failedMessageId, FailedMessageResponseMatcher.aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello World"))
                .withBroker(equalTo("some-broker"))
                .withDestination(equalTo(Optional.of("some-queue")))
                .withProperties(allOf(hasEntry("foo", "bar"), hasEntry("ham", "eggs")))
                .withProperties(hasEntry("count", 6)));
    }
}
