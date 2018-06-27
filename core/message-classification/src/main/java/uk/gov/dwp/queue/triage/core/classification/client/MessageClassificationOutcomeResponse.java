package uk.gov.dwp.queue.triage.core.classification.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;

public class MessageClassificationOutcomeResponse {

    private final boolean matched;
    private final String description;
    private final FailedMessageResponse failedMessageResponse;
    private final String action;

    public MessageClassificationOutcomeResponse(@JsonProperty("matched") boolean matched,
                                                @JsonProperty("description") String description,
                                                @JsonProperty("failedMessageResponse") FailedMessageResponse failedMessageResponse,
                                                @JsonProperty("action") String action) {
        this.matched = matched;
        this.description = description;
        this.failedMessageResponse = failedMessageResponse;
        this.action = action;
    }

    @JsonProperty
    public boolean isMatched() {
        return matched;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public FailedMessageResponse getFailedMessageResponse() {
        return failedMessageResponse;
    }

    @JsonProperty
    public String getAction() {
        return action;
    }
}
