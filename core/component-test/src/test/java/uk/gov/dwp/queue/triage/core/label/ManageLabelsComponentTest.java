package uk.gov.dwp.queue.triage.core.label;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest.newCreateFailedMessageRequest;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class ManageLabelsComponentTest extends BaseCoreComponentTest<FailedMessageResourceStage> {

    private final FailedMessageId failedMessageId = newFailedMessageId();

    @ScenarioStage
    private AddLabelWhenStage addLabelWhenStage;

    @Test
    public void addLabelToFailedMessage() {
        given().aFailedMessage(newCreateFailedMessageRequest().withFailedMessageId(failedMessageId).withBrokerName("some-broker")).exists();

        addLabelWhenStage.when().$IsAddedAsALabelToFailedMessage$("foo", failedMessageId);
        addLabelWhenStage.when().$IsAddedAsALabelToFailedMessage$("bar", failedMessageId);

        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withLabels(containsInAnyOrder("foo", "bar")));

    }

    @Test
    public void removeLabelFromFailedMessage() {
        given().aFailedMessage(newCreateFailedMessageRequest()
                .withFailedMessageId(failedMessageId).withBrokerName("some-broker")
                .withLabel("foo").withLabel("bar")).exists();

        addLabelWhenStage.when().theLabel$IsRemovedFromFAiledMessage$("bar", failedMessageId);

        then().aFailedMessageWithId$Has(failedMessageId, aFailedMessage().withLabels(contains("foo")));
    }


}
