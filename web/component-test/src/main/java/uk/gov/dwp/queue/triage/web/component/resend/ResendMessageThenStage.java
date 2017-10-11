package uk.gov.dwp.queue.triage.web.component.resend;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ThenStage;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@JGivenStage
public class ResendMessageThenStage extends ThenStage<ResendMessageThenStage> {

    @Autowired
    private ResendFailedMessageClient resendFailedMessageClient;

    public ResendMessageThenStage failedMessage$IsResent(FailedMessageId failedMessageId) {
        verify(resendFailedMessageClient).resendFailedMessage(failedMessageId);
        return this;
    }

    public ResendMessageThenStage failedMessage$IsNotResent(FailedMessageId failedMessageId) {
        verify(resendFailedMessageClient, never()).resendFailedMessage(failedMessageId);
        return this;
    }
}
