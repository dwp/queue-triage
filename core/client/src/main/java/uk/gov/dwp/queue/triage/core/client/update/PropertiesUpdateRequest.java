package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropertiesUpdateRequest implements UpdateRequest {

    private final Set<String> deletedProperties;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private final Map<String, Object> updatedProperties;

    public PropertiesUpdateRequest(@JsonProperty("deletedProperties") Set<String> deletedProperties,
                                   @JsonProperty("updatedProperties") Map<String, Object> updatedProperties) {
        this.deletedProperties = deletedProperties;
        this.updatedProperties = updatedProperties;
    }

    /**
     * @return property keys that have been deleted
     */
    public Set<String> getDeletedProperties() {
        return deletedProperties == null ? Collections.emptySet() : new HashSet<>(deletedProperties);
    }

    public Map<String, Object> getUpdatedProperties() {
        return updatedProperties == null ? Collections.emptyMap() : new HashMap<>(updatedProperties);
    }
}
