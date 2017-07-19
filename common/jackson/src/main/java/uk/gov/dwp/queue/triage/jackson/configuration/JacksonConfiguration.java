package uk.gov.dwp.queue.triage.jackson.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.migration.mongo.demo.cxf.client.CxfConfiguration;
import uk.gov.dwp.queue.triage.id.Id;
import uk.gov.dwp.queue.triage.jackson.ISO8601DateFormatWithMilliSeconds;
import uk.gov.dwp.queue.triage.jackson.IdDeserializer;
import uk.gov.dwp.queue.triage.jackson.IdSerializer;

@Configuration
@Import({
        CxfConfiguration.class
})
public class JacksonConfiguration {

    @Bean
    public JacksonJsonProvider jacksonJsonProvider(CxfConfiguration cxfConfiguration, ObjectMapper objectMapper) {
        return cxfConfiguration.providerRegistry().add(new JacksonJsonProvider(objectMapper));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .setDateFormat(new ISO8601DateFormatWithMilliSeconds())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new SimpleModule()
                        .addSerializer(Id.class, new IdSerializer())
                        .addDeserializer(Id.class, new IdDeserializer()));
    }
}
