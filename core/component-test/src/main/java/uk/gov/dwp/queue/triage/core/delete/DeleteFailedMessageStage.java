package uk.gov.dwp.queue.triage.core.delete;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

@JGivenStage
public class DeleteFailedMessageStage extends Stage<DeleteFailedMessageStage> {

    @Autowired
    private DeleteFailedMessageClient deleteFailedMessageClient;

    public DeleteFailedMessageStage theFailedMessageWithId$IsDeleted(FailedMessageId failedMessageId) {
        deleteFailedMessageClient.deleteFailedMessage(failedMessageId);
        return this;
    }
}
