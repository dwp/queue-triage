package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Optional;
import java.util.regex.Pattern;

public class PropertyMatchesPredicate implements FailedMessagePredicate {

    @JsonProperty
    private final String name;
    @JsonProperty
    private final String regex;
    @JsonIgnore
    private final Pattern pattern;

    public PropertyMatchesPredicate(@JsonProperty("name") String name,
                                    @JsonProperty("regex") String regex) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (StringUtils.isBlank(regex)) {
            throw new IllegalArgumentException("regex cannot be null");
        }
        this.name = name;
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        return pattern
                .matcher(getPropertyAsString(failedMessage))
                .matches();
    }

    private String getPropertyAsString(FailedMessage failedMessage) {
        return Optional.ofNullable(failedMessage.getProperty(name))
                .map(String::valueOf)
                .orElse("");
    }

    @Override
    public Description describe(Description description) {
        return description
                .append("property[").append(name).append("] matches ")
                .append(regex);
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }
}
