package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import org.bson.BSON;
import org.bson.Transformer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.id.Id;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.PropertiesConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageMongoDao;
import uk.gov.dwp.queue.triage.core.jackson.configuration.JacksonConfiguration;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static java.time.ZoneOffset.UTC;

@Configuration
@Import(JacksonConfiguration.class)
@EnableConfigurationProperties(DaoProperties.class)
public class DaoConfig {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(UTC));
        BSON.addDecodingHook(LocalDateTime.class, new LocalDateTimeTransformer());
        BSON.addEncodingHook(LocalDateTime.class, new LocalDateTimeTransformer());
        BSON.addDecodingHook(ZonedDateTime.class, new ZonedDateTimeTransformer());
        BSON.addEncodingHook(ZonedDateTime.class, new ZonedDateTimeTransformer());
        BSON.addEncodingHook(Id.class, new IdTransformer());
        BSON.addDecodingHook(Date.class, new ZonedDateTimeTransformer());
    }

    @Bean
    public FailedMessageDao failedMessageDao(MongoClient mongoClient, DaoProperties daoProperties, FailedMessageConverter failedMessageConverter) {
        return new FailedMessageMongoDao(
                mongoClient.getDB(daoProperties.getDbName()).getCollection(daoProperties.getCollection().getFailedMessage()),
                failedMessageConverter
        );
    }

    @Bean
    public FailedMessageConverter failedMessageConverter(DBObjectConverter<Destination> destinationDBObjectConverter, ObjectConverter<Map<String, Object>, String> propertiesObjectConverter) {
        return new FailedMessageConverter(destinationDBObjectConverter, propertiesObjectConverter);
    }

    @Bean
    public PropertiesConverter propertiesConverter(ObjectMapper objectMapper) {
        return new PropertiesConverter(objectMapper);
    }

    @Bean
    public DestinationDBObjectConverter destinationDBObjectConverter() {
        return new DestinationDBObjectConverter();
    }

    public static class LocalDateTimeTransformer implements Transformer {

        @Override
        public Object transform(Object objectToTransform) {
            if (objectToTransform instanceof LocalDateTime) {
                return Date.from(((LocalDateTime)objectToTransform).toInstant(UTC));
            } else if (objectToTransform instanceof Date) {
                return LocalDateTime.ofInstant(((Date) objectToTransform).toInstant(), UTC);
            }
            throw new IllegalArgumentException("LocalDateTimeTransformer can only be used with LocalDateTime or Date");
        }
    }

    public static class ZonedDateTimeTransformer implements Transformer {

        @Override
        public Object transform(Object objectToTransform) {
            if (objectToTransform instanceof ZonedDateTime) {
                return Date.from(((ZonedDateTime)objectToTransform).toInstant());
            } else if (objectToTransform instanceof Date) {
                return ZonedDateTime.ofInstant(((Date) objectToTransform).toInstant(), UTC);
            }
            throw new IllegalArgumentException("ZonedDateTimeTransformer can only be used with ZonedDateTime or Date");
        }
    }

    public static class IdTransformer implements Transformer {

        @Override
        public Object transform(Object objectToTransform) {
            if (objectToTransform instanceof Id) {
                return ((Id)objectToTransform).getId().toString();
            }
            throw new IllegalArgumentException("IdTransformer can only be used with instances of Id");
        }
    }
}
