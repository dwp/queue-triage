package uk.gov.dwp.queue.triage.web.server.w2ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public abstract class W2UIDomainObject<T> {

    @JsonUnwrapped
    protected final T object;

    public W2UIDomainObject(T object) {
        this.object = object;
    }

    @JsonProperty("recid")
    public abstract String getRecId();
}
