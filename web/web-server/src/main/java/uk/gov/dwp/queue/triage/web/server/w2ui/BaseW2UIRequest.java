package uk.gov.dwp.queue.triage.web.server.w2ui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public static class Base2UIRequestBuilder<T extends Base2UIRequestBuilder> {
        protected String cmd;
        protected int limit = 0;
        protected int offset = 0;
        protected List<String> selectedRecords = new ArrayList<>();

        public T withCmd(String cmd) {
            this.cmd = cmd;
            return (T)this;
        }

        public T withLimit(int limit) {
            this.limit = limit;
            return (T)this;
        }

        public T withOffset(int offset) {
            this.offset = offset;
            return (T)this;
        }

        public T withSelectedRecords(List<String> selectedRecords) {
            this.selectedRecords = selectedRecords;
            return (T)this;
        }

        public T withSelectedRecords(FailedMessageId...selectedRecords) {
            this.selectedRecords.addAll(Stream.of(selectedRecords).map(FailedMessageId::toString).collect(Collectors.toList()));
            return (T)this;
        }

        public T withSelectedRecord(FailedMessageId selectedRecord) {
            this.selectedRecords.add(selectedRecord.toString());
            return (T)this;
        }
    }
}
