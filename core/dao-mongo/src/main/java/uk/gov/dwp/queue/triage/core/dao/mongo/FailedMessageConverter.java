package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Map;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder.newFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.fromString;

public class FailedMessageConverter implements DBObjectWithIdConverter<FailedMessage, FailedMessageId> {

    public static final String DESTINATION = "destination";
    public static final String SENT_DATE_TIME = "sentDateTime";
    public static final String FAILED_DATE_TIME = "failedDateTime";
    public static final String CONTENT = "content";
    public static final String PROPERTIES = "properties";

    private final ObjectConverter<Destination, DBObject> destinationDBObjectMapper;
    private final ObjectConverter<Map<String, Object>, String> propertiesMongoMapper;

    public FailedMessageConverter(DBObjectConverter<Destination> destinationDBObjectMapper,
                                  ObjectConverter<Map<String, Object>, String> propertiesMongoMapper) {
        this.destinationDBObjectMapper = destinationDBObjectMapper;
        this.propertiesMongoMapper = propertiesMongoMapper;
    }

    @Override
    public FailedMessage convertToObject(DBObject dbObject) {
        if (dbObject == null) {
            return null;
        }
        BasicDBObject basicDBObject = (BasicDBObject)dbObject;
        return newFailedMessage()
                .withFailedMessageId(getFailedMessageId(basicDBObject))
                .withDestination(getDestination(basicDBObject))
                .withSentDateTime(getSentDateTime(basicDBObject))
                .withFailedDateTime(getFailedDateTime(basicDBObject))
                .withContent(getContent(basicDBObject))
                .withProperties(propertiesMongoMapper.convertToObject(basicDBObject.getString(PROPERTIES)))
                .build();
    }

    public FailedMessageId getFailedMessageId(BasicDBObject basicDBObject) {
        return fromString(basicDBObject.getString("_id"));
    }

    public Destination getDestination(BasicDBObject basicDBObject) {
        return destinationDBObjectMapper.convertToObject((DBObject) basicDBObject.get(DESTINATION));
    }

    public String getContent(BasicDBObject basicDBObject) {
        return basicDBObject.getString(CONTENT);
    }

    public Instant getFailedDateTime(BasicDBObject basicDBObject) {
        return (Instant) basicDBObject.get(FAILED_DATE_TIME);
    }

    public Instant getSentDateTime(BasicDBObject basicDBObject) {
        return (Instant) basicDBObject.get(SENT_DATE_TIME);
    }

    @Override
    public BasicDBObject convertFromObject(FailedMessage item) {
        return createId(item.getFailedMessageId())
                .append(DESTINATION, destinationDBObjectMapper.convertFromObject(item.getDestination()))
                .append(SENT_DATE_TIME, item.getSentAt())
                .append(FAILED_DATE_TIME, item.getFailedAt())
                .append(CONTENT, item.getContent())
                .append(PROPERTIES, propertiesMongoMapper.convertFromObject(item.getProperties()));
    }

    @Override
    public BasicDBObject createId(FailedMessageId failedMessageId) {
        return new BasicDBObject("_id", failedMessageId.getId().toString());
    }
}
