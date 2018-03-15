package uk.gov.dwp.queue.triage.web.server.api.resend;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.web.server.w2ui.BaseW2UIRequest;

import java.util.List;

public class ResendRequest extends BaseW2UIRequest {

    ResendRequest(@JsonProperty("cmd") String cmd,
                  @JsonProperty("limit") Integer limit,
                  @JsonProperty("offset") Integer offset,
                  @JsonProperty("selected") List<String> selected) {
        super(cmd, limit, offset, selected);
    }

    public static ResendRequestBuilder newDeleteRequest() {
        return new ResendRequestBuilder();
    }

    public static class ResendRequestBuilder extends Base2UIRequestBuilder<ResendRequestBuilder> {

        public ResendRequest build() {
            return new ResendRequest(
                    cmd,
                    limit,
                    offset,
                    selectedRecords
            );
        }
    }
}
