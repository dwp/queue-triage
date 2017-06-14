package uk.gov.dwp.queue.triage.core.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.gov.dwp.queue.triage.core.client.Id;

import java.io.IOException;

public class IdSerializer extends JsonSerializer<Id> {

    @Override
    public void serialize(Id value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getId().toString());
    }
}
