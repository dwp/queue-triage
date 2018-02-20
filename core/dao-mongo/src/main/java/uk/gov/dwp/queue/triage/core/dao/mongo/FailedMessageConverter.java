package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder.newFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.fromString;

public class FailedMessageConverter implements DBObjectWithIdConverter<FailedMessage, FailedMessageId> {

    public static final String DESTINATION = "destination";
    public static final String SENT_DATE_TIME = "sentDateTime";
    public static final String FAILED_DATE_TIME = "failedDateTime";
    public static final String CONTENT = "content";
    public static final String PROPERTIES = "properties";
    public static final String STATUS_HISTORY = "statusHistory";
    public static final String LABELS = "labels";
    public static final String JMS_MESSAGE_ID = "jmsMessageId";

    private final DBObjectConverter<Destination> destinationDBObjectMapper;
    private final DBObjectConverter<StatusHistoryEvent> statusHistoryEventDBObjectMapper;
    private final ObjectConverter<Map<String, Object>, String> propertiesMongoMapper;

    public FailedMessageConverter(DBObjectConverter<Destination> destinationDBObjectMapper,
                                  DBObjectConverter<StatusHistoryEvent> statusHistoryEventDBObjectMapper,
                                  ObjectConverter<Map<String, Object>, String> propertiesMongoMapper) {
        this.destinationDBObjectMapper = destinationDBObjectMapper;
        this.propertiesMongoMapper = propertiesMongoMapper;
        this.statusHistoryEventDBObjectMapper = statusHistoryEventDBObjectMapper;
    }

    @Override
    public FailedMessage convertToObject(DBObject dbObject) {
        if (dbObject == null) {
            return null;
        }
        BasicDBObject basicDBObject = (BasicDBObject)dbObject;
        return newFailedMessage()
                .withFailedMessageId(getFailedMessageId(basicDBObject))
                .withJmsMessageId(getJmsMessageId(basicDBObject))
                .withDestination(getDestination(basicDBObject))
                .withSentDateTime(getSentDateTime(basicDBObject))
                .withFailedDateTime(getFailedDateTime(basicDBObject))
                .withContent(getContent(basicDBObject))
                .withStatusHistoryEvent(getStatusHistoryEvent(basicDBObject))
                .withProperties(propertiesMongoMapper.convertToObject(basicDBObject.getString(PROPERTIES)))
                .withLabels(getLabels(basicDBObject))
                .build();
    }

    public List<FailedMessage> convertToList(DBCursor dbCursor) {
        List<FailedMessage> responses = new ArrayList<>();
        for (DBObject dbObject : dbCursor) {
            responses.add(convertToObject(dbObject));
        }
        dbCursor.close();
        return responses;
    }

    public StatusHistoryEvent getStatusHistoryEvent(BasicDBObject basicDBObject) {
        List statusHistory = (List)basicDBObject.get(STATUS_HISTORY);
        return statusHistoryEventDBObjectMapper.convertToObject((BasicDBObject)statusHistory.get(0));
    }

    public FailedMessageId getFailedMessageId(BasicDBObject basicDBObject) {
        return fromString(basicDBObject.getString("_id"));
    }

    private String getJmsMessageId(BasicDBObject basicDBObject) {
        return basicDBObject.getString(JMS_MESSAGE_ID);
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

    private Set<String> getLabels(BasicDBObject basicDBObject) {
        return new HashSet<>((List<String>)basicDBObject.get(LABELS));
    }

    @Override
    public BasicDBObject convertFromObject(FailedMessage item) {
        return createId(item.getFailedMessageId())
                .append(JMS_MESSAGE_ID, item.getJmsMessageId())
                .append(DESTINATION, destinationDBObjectMapper.convertFromObject(item.getDestination()))
                .append(SENT_DATE_TIME, item.getSentAt())
                .append(FAILED_DATE_TIME, item.getFailedAt())
                .append(CONTENT, item.getContent())
                .append(PROPERTIES, propertiesMongoMapper.convertFromObject(item.getProperties()))
                .append(STATUS_HISTORY, Collections.singletonList(statusHistoryEventDBObjectMapper.convertFromObject(item.getStatusHistoryEvent())))
                .append(LABELS, DBObjectConverter.toBasicDBList(item.getLabels()))
                ;
    }

    @Override
    public BasicDBObject createId(FailedMessageId failedMessageId) {
        return new BasicDBObject("_id", failedMessageId.getId().toString());
    }
}
