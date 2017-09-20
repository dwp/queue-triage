package uk.gov.dwp.queue.triage.core.label;


import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

@JGivenStage
public class AddLabelWhenStage extends WhenStage<AddLabelWhenStage> {

    @Autowired
    private TestRestTemplate restTemplate;

    public AddLabelWhenStage $IsAddedAsALabelToFailedMessage$(String label, FailedMessageId failedMessageId) {
        restTemplate.put(
                "/core/failed-message/label/{failedMessageId}/{label}",
                null,
                ImmutableMap.of("failedMessageId", failedMessageId, "label", label)
        );
        return this;
    }

    public AddLabelWhenStage theLabel$IsRemovedFromFAiledMessage$(String label, FailedMessageId failedMessageId) {
        restTemplate.delete(
                "/core/failed-message/label/{failedMessageId}/{label}",
                ImmutableMap.of("failedMessageId", failedMessageId, "label", label)
        );
        return this;
    }
}
