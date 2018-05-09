package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type"
)
@JsonSubTypes({
        @Type(value = PropertiesUpdateRequest.class, name = "properties"),
        @Type(value = ContentUpdateRequest.class, name = "content"),
        @Type(value = DestinationUpdateRequest.class, name = "destination"),
        @Type(value = StatusUpdateRequest.class, name = "status")
})
public interface UpdateRequest {
}
