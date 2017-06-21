package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;

import java.time.ZonedDateTime;
import java.util.Map;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.fromString;

public class FailedMessageConverter implements DBObjectWithIdConverter<FailedMessage, FailedMessageId> {

    static final String DESTINATION = "destination";
    static final String SENT_DATE_TIME = "sentDateTime";
    static final String FAILED_DATE_TIME = "failedDateTime";
    static final String CONTENT = "content";
    static final String PROPERTIES = "properties";

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
        return aFailedMessage()
                .withFailedMessageId(fromString(basicDBObject.getString("_id")))
                .withDestination(destinationDBObjectMapper.convertToObject((DBObject) basicDBObject.get(DESTINATION)))
                .withSentDateTime((ZonedDateTime)basicDBObject.get(SENT_DATE_TIME))
                .withFailedDateTime((ZonedDateTime)basicDBObject.get(FAILED_DATE_TIME))
                .withContent(basicDBObject.getString(CONTENT))
                .withProperties(propertiesMongoMapper.convertToObject(basicDBObject.getString(PROPERTIES)))
                .build();
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
