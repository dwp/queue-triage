package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;

public class ContentMatchesJsonPath implements FailedMessagePredicate {

    @JsonProperty
    private final String jsonPath;
    @JsonProperty
    private final String regex;
    private final Pattern pattern;
    private final ParseContext parseContext = JsonPath.using(defaultConfiguration().addOptions(SUPPRESS_EXCEPTIONS));

    public ContentMatchesJsonPath(@JsonProperty("jsonPath") String jsonPath,
                                  @JsonProperty("regex") String regex) {
        this.jsonPath = jsonPath;
        this.regex = regex;
        this.pattern = Pattern.compile(regex);

    }

    @Override
    public boolean test(FailedMessage failedMessage) {
        final Object object = Optional.ofNullable(failedMessage.getContent())
                .filter(StringUtils::isNotEmpty)
                .map(parseContext::parse)
                .map(content -> content.read(jsonPath))
                .orElse(null);
        if (object instanceof JSONArray) {
            for (Object item: ((JSONArray)object)) {
                if (pattern.matcher(String.valueOf(item)).matches()) {
                    return true;
                }
            }
        } else {
            return pattern
                    .matcher(String.valueOf(object))
                    .matches();
        }
        return false;
    }
}
