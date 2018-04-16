package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder.newFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.fromString;

public class FailedMessageConverter implements DocumentWithIdConverter<FailedMessage, FailedMessageId> {

    public static final String DESTINATION = "destination";
    public static final String SENT_DATE_TIME = "sentDateTime";
    public static final String FAILED_DATE_TIME = "failedDateTime";
    public static final String CONTENT = "content";
    public static final String PROPERTIES = "properties";
    public static final String STATUS_HISTORY = "statusHistory";
    public static final String LABELS = "labels";
    public static final String JMS_MESSAGE_ID = "jmsMessageId";

    private final DocumentConverter<Destination> destinationDocumentConverter;
    private final DocumentConverter<StatusHistoryEvent> statusHistoryEventDocumentConverter;
    private final ObjectConverter<Map<String, Object>, String> propertiesMongoMapper;

    public FailedMessageConverter(DocumentConverter<Destination> destinationDocumentConverter,
                                  DocumentConverter<StatusHistoryEvent> statusHistoryEventDocumentConverter,
                                  ObjectConverter<Map<String, Object>, String> propertiesMongoMapper) {
        this.destinationDocumentConverter = destinationDocumentConverter;
        this.propertiesMongoMapper = propertiesMongoMapper;
        this.statusHistoryEventDocumentConverter = statusHistoryEventDocumentConverter;
    }

    @Override
    public FailedMessage convertToObject(Document document) {
        if (document == null) {
            return null;
        }
        return newFailedMessage()
                .withFailedMessageId(getFailedMessageId(document))
                .withJmsMessageId(getJmsMessageId(document))
                .withDestination(getDestination(document))
                .withSentDateTime(getSentDateTime(document))
                .withFailedDateTime(getFailedDateTime(document))
                .withContent(getContent(document))
                .withStatusHistoryEvent(getStatusHistoryEvent(document))
                .withProperties(propertiesMongoMapper.convertToObject(document.getString(PROPERTIES)))
                .withLabels(getLabels(document))
                .build();
    }

    public StatusHistoryEvent getStatusHistoryEvent(Document document) {
        List statusHistory = (List)document.get(STATUS_HISTORY);
        return statusHistoryEventDocumentConverter.convertToObject((Document)statusHistory.get(0));
    }

    public FailedMessageId getFailedMessageId(Document document) {
        return fromString(document.getString("_id"));
    }

    private String getJmsMessageId(Document document) {
        return document.getString(JMS_MESSAGE_ID);
    }

    public Destination getDestination(Document document) {
        return destinationDocumentConverter.convertToObject((Document) document.get(DESTINATION));
    }

    public String getContent(Document document) {
        return document.getString(CONTENT);
    }

    public Instant getFailedDateTime(Document document) {
        return (Instant) document.get(FAILED_DATE_TIME);
    }

    public Instant getSentDateTime(Document document) {
        return (Instant) document.get(SENT_DATE_TIME);
    }

    private Set<String> getLabels(Document document) {
        return new HashSet<>((List<String>) document.get(LABELS));
    }

    @Override
    public Document convertFromObject(FailedMessage item) {
        return createId(item.getFailedMessageId())
                .append(JMS_MESSAGE_ID, item.getJmsMessageId())
                .append(DESTINATION, destinationDocumentConverter.convertFromObject(item.getDestination()))
                .append(SENT_DATE_TIME, item.getSentAt())
                .append(FAILED_DATE_TIME, item.getFailedAt())
                .append(CONTENT, item.getContent())
                .append(PROPERTIES, propertiesMongoMapper.convertFromObject(item.getProperties()))
                .append(STATUS_HISTORY, Collections.singletonList(statusHistoryEventDocumentConverter.convertFromObject(item.getStatusHistoryEvent())))
                .append(LABELS, DocumentConverter.toBasicDBList(item.getLabels()))
                ;
    }

    public Document convertForUpdate(FailedMessage item) {
        return new Document()
                .append(JMS_MESSAGE_ID, item.getJmsMessageId())
                .append(DESTINATION, destinationDocumentConverter.convertFromObject(item.getDestination()))
                .append(CONTENT, item.getContent())
                .append(PROPERTIES, propertiesMongoMapper.convertFromObject(item.getProperties()))
                ;
    }

    @Override
    public Document createId(FailedMessageId failedMessageId) {
        return new Document("_id", failedMessageId.getId().toString());
    }
}
