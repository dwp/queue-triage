package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.service.FailedMessageLabelService;

public class LabelMessageAction implements FailedMessageAction {

    private final String label;
    private final FailedMessageLabelService failedMessageLabelService;

    public LabelMessageAction(@JsonProperty("label") String label,
                              @JacksonInject FailedMessageLabelService failedMessageLabelService) {
        this.label = label;
        this.failedMessageLabelService = failedMessageLabelService;
    }

    @Override
    public void accept(FailedMessage failedMessage) {
        failedMessageLabelService.addLabel(failedMessage.getFailedMessageId(), label);
    }

    @JsonProperty
    public String getLabel() {
        return label;
    }
}
