package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.List;

public class ChainedFailedMessageAction implements FailedMessageAction {

    @JsonProperty
    private final List<FailedMessageAction> actions;

    public ChainedFailedMessageAction(@JsonProperty("actions") List<FailedMessageAction> actions) {
        this.actions = actions;
    }

    @Override
    public void accept(FailedMessage failedMessage) {
        actions.forEach(action -> action.accept(failedMessage));
    }

    List<FailedMessageAction> getActions() {
        return actions;
    }
}
