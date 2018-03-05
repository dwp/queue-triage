package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.core.classification.predicate.OrPredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Consumer;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_action"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChainedFailedMessageAction.class, name = "chained"),
        @JsonSubTypes.Type(value = DeleteMessageAction.class, name = "delete"),
        @JsonSubTypes.Type(value = LabelMessageAction.class, name = "label"),
        @JsonSubTypes.Type(value = ResendFailedMessageAction.class, name = "resend")
})
public interface FailedMessageAction extends Consumer<FailedMessage> {
}
