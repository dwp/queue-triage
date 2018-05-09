package uk.gov.dwp.queue.triage.core.label;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.CoreComponentTestBase;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageThenStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ManageLabelsComponentTest
        extends CoreComponentTestBase<FailedMessageResourceStage, AddLabelWhenStage, SearchFailedMessageThenStage> {

    private final FailedMessageId failedMessageId = newFailedMessageId();

    @Test
    public void addLabelToFailedMessage() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest().withFailedMessageId(failedMessageId).withBrokerName("some-broker"));

        when().$IsAddedAsALabelToFailedMessage$("foo", failedMessageId);
        when().$IsAddedAsALabelToFailedMessage$("bar", failedMessageId);

        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withLabels(containsInAnyOrder("foo", "bar")));
    }

    @Test
    public void replaceLabelsOnAFailedMessage() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest().withFailedMessageId(failedMessageId).withBrokerName("some-broker").withLabel("something"));

        when().failedMessage$HasTheFollowingLabelsSet$(failedMessageId, "foo", "bar");

        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withLabels(containsInAnyOrder("foo", "bar")));
    }

    @Test
    public void replaceTheLabelsOnAFailedMessageWithAnEmptySet() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest().withFailedMessageId(failedMessageId).withBrokerName("some-broker").withLabel("something"));

        when().failedMessage$HasTheFollowingLabelsSet$(failedMessageId);

        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withLabels(emptyIterable()));
    }

    @Test
    public void removeLabelFromFailedMessage() {
        given().aFailedMessage$Exists(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId).withBrokerName("some-broker")
                .withLabel("foo").withLabel("bar"));

        when().theLabel$IsRemovedFromFAiledMessage$("bar", failedMessageId);

        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withLabels(contains("foo")));
    }
}
