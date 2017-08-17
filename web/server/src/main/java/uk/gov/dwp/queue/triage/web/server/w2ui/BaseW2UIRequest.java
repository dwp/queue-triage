package uk.gov.dwp.queue.triage.web.server.w2ui;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BaseW2UIRequest {

    @JsonProperty
    private final List<String> selected;
    @JsonProperty
    private final String cmd;
    @JsonProperty
    private final Integer limit;
    @JsonProperty
    private final Integer offset;

    public BaseW2UIRequest(@JsonProperty("cmd") String cmd,
                           @JsonProperty("limit") Integer limit,
                           @JsonProperty("offset") Integer offset,
                           @JsonProperty("selected") List<String> selected) {
        this.cmd = cmd;
        this.limit = limit;
        this.offset = offset;
        this.selected = selected;
    }

    public String getCmd() {
        return cmd;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public List<String> getSelected() {
        return selected;
    }
}
