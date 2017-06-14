package uk.gov.dwp.queue.triage.core.jackson.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.client.Id;
import uk.gov.dwp.queue.triage.core.jackson.ISO8601DateFormatWithMilliSeconds;
import uk.gov.dwp.queue.triage.core.jackson.IdDeserializer;
import uk.gov.dwp.queue.triage.core.jackson.IdSerializer;

@Configuration
public class JacksonConfiguration {

    @Bean
    public JacksonJsonProvider jacksonJsonProvider(ObjectMapper objectMapper) {
        return new JacksonJsonProvider(objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .setDateFormat(new ISO8601DateFormatWithMilliSeconds())
                .registerModule(new JavaTimeModule())
                .registerModule(new SimpleModule()
                        .addSerializer(Id.class, new IdSerializer())
                        .addDeserializer(Id.class, new IdDeserializer()));
    }
}
