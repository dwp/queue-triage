package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;

public class ContentContainsJsonPath implements FailedMessagePredicate {

    @JsonProperty
    private final String jsonPath;
    private final ParseContext parseContext = JsonPath.using(defaultConfiguration());

    public ContentContainsJsonPath(@JsonProperty("jsonPath") String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        if (StringUtils.isNotEmpty(failedMessage.getContent())) {
            try {
                final Object object = parseContext.parse(failedMessage.getContent()).read(jsonPath);
                if (object instanceof JSONArray) {
                    return !((JSONArray)object).isEmpty();
                } else {
                    return true;
                }
            } catch (PathNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Description describe(Description description) {
        return description.append("content contains json path ").append(jsonPath);
    }

    @Override
    public String toString() {
        return describe(new StringDescription()).toString();
    }
}
