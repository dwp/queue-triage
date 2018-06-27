package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public class PropertyExistsPredicate implements FailedMessagePredicate {

    @JsonProperty
    private final String propertyName;

    public PropertyExistsPredicate(@JsonProperty("propertyName") String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("propertyName cannot be blank");
        }
        this.propertyName = propertyName;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return failedMessage.getProperties().containsKey(propertyName);
    }

    @Override
    public Description describe(Description description) {
        return description
                .append("property[").append(propertyName).append("] exists");
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }
}
