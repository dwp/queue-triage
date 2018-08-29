package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Objects;

public class PropertyEqualToPredicate implements FailedMessagePredicate {

    @JsonProperty
    private final String name;
    @JsonProperty
    private final Object value;

    public PropertyEqualToPredicate(@JsonProperty("name") String name,
                                    @JsonProperty("value") Object value) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        this.value = Objects.requireNonNull(value, "value cannot be null");
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return this.value.equals(failedMessage.getProperty(name));
    }

    @Override
    public Description describe(Description description) {
        return description
                .append("property[").append(name).append("] = ")
                .append(value);
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }
}
