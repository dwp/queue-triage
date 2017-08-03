package uk.gov.dwp.queue.triage.core.resend;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

@JGivenStage
public class ResendFailedMessageWhenStage extends Stage<ResendFailedMessageWhenStage> {

    @Autowired
    private ResendFailedMessageClient resendFailedMessageClient;

    public ResendFailedMessageWhenStage aFailedMessageWithId$IsResent(FailedMessageId failedMessageId) {
        resendFailedMessageClient.resendFailedMessage(failedMessageId);
        return this;
    }
}
