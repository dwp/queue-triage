package uk.gov.dwp.queue.triage.web.server.w2ui;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class W2UIResponse<T> {

    @JsonProperty
    private final Collection<T> records;

    private W2UIResponse(Collection<T> records) {
        this.records = records;
    }

    public static <T> W2UIResponse<T> success(Collection<T> records) {
        return new W2UIResponse<>(records);
    }

    @JsonProperty
    public String getStatus() {
        return "success";
    }

    @JsonProperty
    public int getTotal() {
        return Optional.ofNullable(records).orElse(Collections.emptyList()).size();
    }

    public Collection<T> getRecords() {
        return records;
    }
}
