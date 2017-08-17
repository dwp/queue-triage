package uk.gov.dwp.queue.triage.web.server.w2ui;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class W2UIResponse<T> {

    @JsonProperty
    private final String status;
    @JsonProperty
    private final int total;
    @JsonProperty
    private final Collection<T> records;

    public W2UIResponse(String status, int total, Collection<T> records) {
        this.status = status;
        this.total = total;
        this.records = records;
    }

    public String getStatus() {
        return status;
    }

    public int getTotal() {
        return total;
    }

    public Collection<T> getRecords() {
        return records;
    }
}
