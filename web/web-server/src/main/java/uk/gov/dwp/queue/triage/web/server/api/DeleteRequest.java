package uk.gov.dwp.queue.triage.web.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.web.server.w2ui.BaseW2UIRequest;

import java.util.List;

public class DeleteRequest extends BaseW2UIRequest {

    DeleteRequest(@JsonProperty("cmd") String cmd,
                  @JsonProperty("limit") Integer limit,
                  @JsonProperty("offset") Integer offset,
                  @JsonProperty("selected") List<String> selected) {
        super(cmd, limit, offset, selected);
    }

    public static DeleteRequestBuilder newDeleteRequest() {
        return new DeleteRequestBuilder();
    }

    public static class DeleteRequestBuilder extends Base2UIRequestBuilder<DeleteRequestBuilder> {

        public DeleteRequest build() {
            return new DeleteRequest(
                    cmd,
                    limit,
                    offset,
                    selectedRecords
            );
        }
    }
}
