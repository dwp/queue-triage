package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.bson.BSON;
import org.bson.Transformer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.PropertiesConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageMongoDao;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDBObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.RemoveRecordsQueryFactory;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.Id;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static java.time.ZoneOffset.UTC;

@Configuration
@Import(JacksonConfiguration.class)
@EnableConfigurationProperties(MongoDaoProperties.class)
public class MongoDaoConfig {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(UTC));
        BSON.addDecodingHook(LocalDateTime.class, new LocalDateTimeTransformer());
        BSON.addEncodingHook(LocalDateTime.class, new LocalDateTimeTransformer());
        BSON.addDecodingHook(Instant.class, new InstantTransformer());
        BSON.addEncodingHook(Instant.class, new InstantTransformer());
        BSON.addEncodingHook(Id.class, new IdTransformer());
        BSON.addDecodingHook(Date.class, new InstantTransformer());
    }

    @Bean
    @DependsOn("mongoDaoProperties")
    public MongoClient mongoClient(MongoDaoProperties mongoDaoProperties) {
        return new MongoClient(
                new ServerAddress(mongoDaoProperties.getHost(), mongoDaoProperties.getPort()),
                mongoDaoProperties.mongoClientOptions()
        );
    }

    @Bean
    @DependsOn("mongoDaoProperties")
    public FailedMessageDao failedMessageDao(MongoClient mongoClient,
                                             MongoDaoProperties mongoDaoProperties,
                                             FailedMessageConverter failedMessageConverter,
                                             DBObjectConverter<FailedMessageStatus> failedMessageStatusDBObjectConverter) {
        return new FailedMessageMongoDao(
                mongoClient.getDB(mongoDaoProperties.getDbName()).getCollection(mongoDaoProperties.getFailedMessage().getName()),
                failedMessageConverter,
                failedMessageStatusDBObjectConverter, new RemoveRecordsQueryFactory());
    }

    @Bean
    public FailedMessageConverter failedMessageConverter(DBObjectConverter<Destination> destinationDBObjectConverter,
                                                         DBObjectConverter<FailedMessageStatus> failedMessageStatusDBObjectConverter,
                                                         ObjectConverter<Map<String, Object>, String> propertiesObjectConverter) {
        return new FailedMessageConverter(destinationDBObjectConverter, failedMessageStatusDBObjectConverter, propertiesObjectConverter);
    }

    @Bean
    public PropertiesConverter propertiesConverter(ObjectMapper objectMapper) {
        return new PropertiesConverter(objectMapper);
    }

    @Bean
    public DestinationDBObjectConverter destinationDBObjectConverter() {
        return new DestinationDBObjectConverter();
    }

    @Bean
    public FailedMessageStatusDBObjectConverter failedMessageStatusDBObjectConverter() {
        return new FailedMessageStatusDBObjectConverter();
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

    public static class InstantTransformer implements Transformer {

        @Override
        public Object transform(Object objectToTransform) {
            if (objectToTransform instanceof Instant) {
                return Date.from(((Instant)objectToTransform));
            } else if (objectToTransform instanceof Date) {
                return ((Date) objectToTransform).toInstant();
            }
            throw new IllegalArgumentException("InstantTransformer can only be used with Instant or Date");
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
