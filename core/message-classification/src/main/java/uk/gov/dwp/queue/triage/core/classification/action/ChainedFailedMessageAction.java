package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return actions.stream().map(Object::toString).collect(Collectors.joining(" AND "));
    }
}
