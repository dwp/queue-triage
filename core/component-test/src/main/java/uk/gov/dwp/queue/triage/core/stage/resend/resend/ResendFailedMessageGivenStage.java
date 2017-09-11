package uk.gov.dwp.queue.triage.core.stage.resend.resend;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import uk.gov.dwp.queue.triage.jgiven.GivenStage;

@JGivenStage
public class ResendFailedMessageGivenStage extends GivenStage<ResendFailedMessageGivenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public ResendFailedMessageGivenStage theResendFailedMessageJobIsPausedForBroker$(String brokerName) {
        testRestTemplate.put(
                "/core/admin/executor/message-resend/" + brokerName + "/pause",
                HttpEntity.EMPTY
        );
        return this;
    }
}
