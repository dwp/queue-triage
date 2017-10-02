package uk.gov.dwp.queue.triage.web.server.list;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FailedMessageListItem {

    @JsonProperty
    private final String recid;
    @JsonProperty
    private final String content;
    @JsonProperty
    private final String broker;
    @JsonProperty
    private final String destination;
    @JsonProperty
    private final String sentDateTime;
    @JsonProperty
    private final String failedDateTime;
    @JsonProperty
    private final String labels;

    public FailedMessageListItem(String recid,
                                 String content,
                                 String broker,
                                 String destination,
                                 String sentDateTime,
                                 String failedDateTime,
                                 String labels) {
        this.recid = recid;
        this.content = content;
        this.broker = broker;
        this.destination = destination;
        this.sentDateTime = sentDateTime;
        this.failedDateTime = failedDateTime;
        this.labels = labels;
    }
}
