package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FailedMessageUpdateRequest {

    private final List<UpdateRequest> updateRequests;

    FailedMessageUpdateRequest(@JsonProperty("updateRequests") List<UpdateRequest> updateRequests) {
        this.updateRequests = updateRequests;
    }

    public List<UpdateRequest> getUpdateRequests() {
        return updateRequests;
    }

    public static FailedMessageUpdateRequestBuilder aNewFailedMessageUpdateRequest() {
        return new FailedMessageUpdateRequestBuilder();
    }

    public static class FailedMessageUpdateRequestBuilder {
        private List<UpdateRequest> updateRequests = new ArrayList<>();

        public FailedMessageUpdateRequestBuilder withUpdateRequests(List<UpdateRequest> updateRequests) {
            this.updateRequests = updateRequests;
            return this;
        }

        public FailedMessageUpdateRequestBuilder withUpdateRequest(UpdateRequest updateRequest) {
            this.updateRequests.add(updateRequest);
            return this;
        }

        public FailedMessageUpdateRequest build() {
            return new FailedMessageUpdateRequest(new ArrayList<>(updateRequests));
        }
    }
}
