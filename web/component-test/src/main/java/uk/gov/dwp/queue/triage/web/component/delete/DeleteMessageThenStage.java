package uk.gov.dwp.queue.triage.web.component.delete;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ThenStage;

@JGivenStage
public class DeleteMessageThenStage extends ThenStage<DeleteMessageThenStage> {

    @Autowired
    private DeleteFailedMessageClient deleteFailedMessageClient;

    public DeleteMessageThenStage failedMessage$IsDeleted(FailedMessageId failedMessageId) {
        Mockito.verify(deleteFailedMessageClient).deleteFailedMessage(failedMessageId);
        return this;
    }
}
