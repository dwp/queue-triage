package uk.gov.dwp.queue.triage.web.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.server.w2ui.BaseW2UIRequest;

import java.util.List;

public class LabelRequest extends BaseW2UIRequest {

    private final List<Change> changes;

    public LabelRequest(@JsonProperty("cmd") String cmd,
                        @JsonProperty("limit") Integer limit,
                        @JsonProperty("offset") Integer offset,
                        @JsonProperty("selected") List<String> selected,
                        @JsonProperty("changes") List<Change> changes) {
        super(cmd, limit, offset, selected);
        this.changes = changes;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public static class Change {

        private final String recid;
        private final String labels;

        public Change(@JsonProperty("recid") String recid,
                      @JsonProperty("labels") String labels) {
            this.recid = recid;
            this.labels = labels;
        }

        public String getRecid() {
            return recid;
        }

        public String getLabels() {
            return labels;
        }

        public static ChangeBuilder newChange() {
            return new ChangeBuilder();
        }

        public static class ChangeBuilder {
            private String recid;
            private String labels;

            public ChangeBuilder withRecid(FailedMessageId failedMessageId) {
                this.recid = failedMessageId.toString();
                return this;
            }

            public ChangeBuilder withLabels(String labels) {
                this.labels = labels;
                return this;
            }

            public Change build() {
                return new Change(recid, labels);
            }
        }
    }
}
