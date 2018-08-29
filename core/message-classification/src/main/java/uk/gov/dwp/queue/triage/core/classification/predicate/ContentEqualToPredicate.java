package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public class ContentEqualToPredicate implements FailedMessagePredicate {

    @JsonProperty
    private final String content;

    public ContentEqualToPredicate(@JsonProperty("content") String content) {
        if (content == null) {
            throw new IllegalArgumentException("content cannot be null");
        }
        this.content = content;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return content.equals(failedMessage.getContent());
    }

    @Override
    public Description describe(Description description) {
        return description.append("content = ").append(content);
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }
}
