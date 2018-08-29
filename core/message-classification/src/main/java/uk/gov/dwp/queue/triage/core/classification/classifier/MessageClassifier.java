package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_classifier"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageClassifierGroup.class, name = "collection"),
        @JsonSubTypes.Type(value = ExecutingMessageClassifier.class, name = "executing"),
        @JsonSubTypes.Type(value = DelegatingMessageClassifier.class, name = "delegating"),
        @JsonSubTypes.Type(value = UnmatchedMessageClassifier.class, name = "unmatched")
})
public interface MessageClassifier {

    MessageClassificationOutcome classify(MessageClassificationContext context);

}
