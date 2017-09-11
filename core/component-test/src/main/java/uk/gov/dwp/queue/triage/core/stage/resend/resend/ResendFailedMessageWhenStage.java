package uk.gov.dwp.queue.triage.core.stage.resend.resend;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

@JGivenStage
public class ResendFailedMessageWhenStage extends Stage<ResendFailedMessageWhenStage> {

    @Autowired
    private ResendFailedMessageClient resendFailedMessageClient;
    @Autowired
    private TestRestTemplate testRestTemplate;

    public ResendFailedMessageWhenStage aFailedMessageWithId$IsMarkedForResend(FailedMessageId failedMessageId) {
        resendFailedMessageClient.resendFailedMessage(failedMessageId);
        return this;
    }

    public ResendFailedMessageWhenStage theResendFailedMessageJobExecutesForBroker$(String brokerName) {
        testRestTemplate.postForLocation(
                "/core/admin/executor/message-resend/" + brokerName + "/execute",
                HttpEntity.EMPTY
        );
        return this;
    }
}
