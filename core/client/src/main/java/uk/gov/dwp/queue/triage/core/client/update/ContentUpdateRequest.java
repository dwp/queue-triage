package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentUpdateRequest implements UpdateRequest {

    private final String content;

    public ContentUpdateRequest(@JsonProperty("content") String content) {
        this.content = content;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }
}
