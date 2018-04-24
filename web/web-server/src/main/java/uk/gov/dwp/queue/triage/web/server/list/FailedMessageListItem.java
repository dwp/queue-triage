package uk.gov.dwp.queue.triage.web.server.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.web.server.api.Constants;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class FailedMessageListItem {

    private static final Predicate<Set<String>> IS_EMPTY = Set::isEmpty;
    private final SearchFailedMessageResponse searchFailedMessageResponse;

    public FailedMessageListItem(SearchFailedMessageResponse searchFailedMessageResponse) {
        this.searchFailedMessageResponse = searchFailedMessageResponse;
    }

    @JsonProperty("recid")
    public String getRecId() {
        return searchFailedMessageResponse.getFailedMessageId().toString();
    }

    public String getContent() {
        return searchFailedMessageResponse.getContent();
    }

    public String getBroker() {
        return searchFailedMessageResponse.getBroker();
    }

    public String getDestination() {
        return searchFailedMessageResponse.getDestination().orElse(null);
    }

    public String getStatusDateTime() {
        return Constants.toIsoDateTimeWithMs(searchFailedMessageResponse.getStatusDateTime()).orElse(null);
    }

    public String getStatus() {
        return searchFailedMessageResponse.getStatus().getDescription();
    }

    public String getLabels() {
        return Optional.ofNullable(searchFailedMessageResponse.getLabels())
                .filter(IS_EMPTY.negate())
                .map(x -> String.join(", ", x))
                .orElse(null);
    }
}
