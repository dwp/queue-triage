package uk.gov.dwp.queue.triage.core.update;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import uk.gov.dwp.queue.triage.core.client.update.FailedMessageUpdateRequest;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

@JGivenStage
public class UpdateFailedMessageWhenStage extends WhenStage<UpdateFailedMessageWhenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public UpdateFailedMessageWhenStage failedMessage$HasTheFollowingUpdates$Submitted(FailedMessageId failedMessageId,
                                                                                       FailedMessageUpdateRequest failedMessageUpdateRequest) {
        testRestTemplate.put("/core/failed-message/update/{failedMessageId}", failedMessageUpdateRequest, failedMessageId);
        return this;
    }
}
