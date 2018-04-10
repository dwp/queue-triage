package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.operation.OrderBy;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDocumentConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.LABELS;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDocumentConverter.LAST_MODIFIED_DATE_TIME;

public class FailedMessageMongoDao implements FailedMessageDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageMongoDao.class);

    private final MongoCollection<Document> collection;
    private final FailedMessageConverter failedMessageConverter;
    private final DocumentConverter<StatusHistoryEvent> failedMessageStatusConverter;
    private final RemoveRecordsQueryFactory removeRecordsQueryFactory;

    public FailedMessageMongoDao(MongoCollection<Document> collection,
                                 FailedMessageConverter failedMessageConverter,
                                 DocumentConverter<StatusHistoryEvent> failedMessageStatusConverter,
                                 RemoveRecordsQueryFactory removeRecordsQueryFactory) {
        this.collection = collection;
        this.failedMessageConverter = failedMessageConverter;
        this.failedMessageStatusConverter = failedMessageStatusConverter;
        this.removeRecordsQueryFactory = removeRecordsQueryFactory;
    }

    @Override
    public void insert(FailedMessage failedMessage) {
        collection.insertOne(failedMessageConverter.convertFromObject(failedMessage));
        LOGGER.debug("1 row inserted with Id: {}", failedMessage.getFailedMessageId());
    }

    @Override
    public void update(FailedMessage failedMessage) {
        collection.updateOne(
                failedMessageConverter.createId(failedMessage.getFailedMessageId()),
                new Document()
                        .append("$push", new Document(STATUS_HISTORY, new Document()
                                .append("$each", singletonList(failedMessageStatusConverter.convertFromObject(failedMessage.getStatusHistoryEvent())))
                                .append("$sort", new Document(LAST_MODIFIED_DATE_TIME, OrderBy.DESC.getIntRepresentation()))))
                        .append("$set", failedMessageConverter.convertForUpdate(failedMessage))
        );
    }

    @Override
    public void updateStatus(FailedMessageId failedMessageId, StatusHistoryEvent statusHistoryEvent) {
        collection.updateOne(
                failedMessageConverter.createId(failedMessageId),
                new Document("$push", new Document(STATUS_HISTORY, new Document()
                        .append("$each", singletonList(failedMessageStatusConverter.convertFromObject(statusHistoryEvent)))
                        .append("$sort", new Document(LAST_MODIFIED_DATE_TIME, OrderBy.DESC.getIntRepresentation()))))
        );
    }

    @Override
    public List<StatusHistoryEvent> getStatusHistory(FailedMessageId failedMessageId) {
        final List<Document> list = collection
                .find(failedMessageConverter.createId(failedMessageId))
                .projection(new Document(STATUS_HISTORY, 1))
                .first()
                .get(STATUS_HISTORY, List.class);
        return list
                .stream()
                .map(failedMessageStatusConverter::convertToObject)
                .collect(Collectors.toList());
    }

    @Override
    public FailedMessage findById(FailedMessageId failedMessageId) {
        return collection.find(failedMessageConverter.createId(failedMessageId)).map(failedMessageConverter::convertToObject).first();
    }

    @Override
    public long findNumberOfMessagesForBroker(String broker) {
        return collection.count(new Document(DESTINATION + "." + BROKER_NAME, broker));
    }

    @Override
    public long removeFailedMessages() {
        return collection.deleteMany(removeRecordsQueryFactory.create()).getDeletedCount();
    }

    @Override
    public void addLabel(FailedMessageId failedMessageId, String label) {
        UpdateResult updateResult = collection.updateOne(
                failedMessageConverter.createId(failedMessageId),
                new Document("$addToSet", new Document(LABELS, label))
        );
        LOGGER.debug("{} rows updated", updateResult.getModifiedCount());
    }

    @Override
    public void setLabels(FailedMessageId failedMessageId, Set<String> labels) {
        UpdateResult updateResult = collection.updateOne(
                failedMessageConverter.createId(failedMessageId),
                new Document("$set", new Document(LABELS, labels))
        );
        LOGGER.debug("{} rows updated", updateResult.getModifiedCount());
    }

    @Override
    public void removeLabel(FailedMessageId failedMessageId, String label) {
        collection.updateOne(
                failedMessageConverter.createId(failedMessageId),
                new Document("$pull", new Document(LABELS, label))
        );
    }
}
