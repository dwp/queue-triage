package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.operation.OrderBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.LABELS;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDBObjectConverter.LAST_MODIFIED_DATE_TIME;

public class FailedMessageMongoDao implements FailedMessageDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageMongoDao.class);
    private static final boolean NO_UPSERT = false;
    private static final boolean SINGLE_ROW = false;

    private final DBCollection collection;
    private final FailedMessageConverter failedMessageConverter;
    private final DBObjectConverter<FailedMessageStatus> failedMessageStatusConverter;
    private final RemoveRecordsQueryFactory removeRecordsQueryFactory;

    public FailedMessageMongoDao(DBCollection collection,
                                 FailedMessageConverter failedMessageConverter,
                                 DBObjectConverter<FailedMessageStatus> failedMessageStatusConverter,
                                 RemoveRecordsQueryFactory removeRecordsQueryFactory) {
        this.collection = collection;
        this.failedMessageConverter = failedMessageConverter;
        this.failedMessageStatusConverter = failedMessageStatusConverter;
        this.removeRecordsQueryFactory = removeRecordsQueryFactory;
    }

    @Override
    public void insert(FailedMessage failedMessage) {
        collection.insert(
                WriteConcern.ACKNOWLEDGED,
                failedMessageConverter.convertFromObject(failedMessage)
        );
        LOGGER.debug("1 row inserted with Id: {}", failedMessage.getFailedMessageId());
    }

    @Override
    public void updateStatus(FailedMessageId failedMessageId, FailedMessageStatus failedMessageStatus) {
        collection.update(
                failedMessageConverter.createId(failedMessageId),
                new BasicDBObject("$push", new BasicDBObject(STATUS_HISTORY, new BasicDBObject()
                        .append("$each", singletonList(failedMessageStatusConverter.convertFromObject(failedMessageStatus)))
                        .append("$sort", new BasicDBObject(LAST_MODIFIED_DATE_TIME, OrderBy.DESC.getIntRepresentation())))),
                NO_UPSERT, SINGLE_ROW, WriteConcern.ACKNOWLEDGED
        );
    }

    @Override
    public List<FailedMessageStatus> getStatusHistory(FailedMessageId failedMessageId) {
        return ((BasicDBList)collection
                .findOne(failedMessageConverter.createId(failedMessageId), new BasicDBObject(STATUS_HISTORY, 1))
                .get(STATUS_HISTORY))
                .stream()
                .map(DBObject.class::cast)
                .map(failedMessageStatusConverter::convertToObject)
                .collect(Collectors.toList());
    }

    @Override
    public FailedMessage findById(FailedMessageId failedMessageId) {
        DBObject failedMessage = collection.findOne(failedMessageConverter.createId(failedMessageId));
        return failedMessageConverter.convertToObject(failedMessage);
    }

    @Override
    public long findNumberOfMessagesForBroker(String broker) {
        return collection.count(new BasicDBObject(DESTINATION + "." + BROKER_NAME, broker));
    }

    @Override
    public int removeFailedMessages() {
        return collection.remove(removeRecordsQueryFactory.create(), WriteConcern.ACKNOWLEDGED).getN();
    }

    @Override
    public void addLabel(FailedMessageId failedMessageId, String label) {
        WriteResult writeResult = collection.update(
                failedMessageConverter.createId(failedMessageId),
                new BasicDBObject("$addToSet", new BasicDBObject(LABELS, label)),
                NO_UPSERT, SINGLE_ROW, WriteConcern.ACKNOWLEDGED
        );
        LOGGER.debug("{} rows updated", writeResult.getN());
    }

    @Override
    public void setLabels(FailedMessageId failedMessageId, Set<String> labels) {
        WriteResult writeResult = collection.update(
                failedMessageConverter.createId(failedMessageId),
                new BasicDBObject("$set", new BasicDBObject(LABELS, labels)),
                NO_UPSERT, SINGLE_ROW, WriteConcern.ACKNOWLEDGED
        );
        LOGGER.debug("{} rows updated", writeResult.getN());
    }

    @Override
    public void removeLabel(FailedMessageId failedMessageId, String label) {
        collection.update(
                failedMessageConverter.createId(failedMessageId),
                new BasicDBObject("$pull", new BasicDBObject(LABELS, label)),
                NO_UPSERT, SINGLE_ROW, WriteConcern.ACKNOWLEDGED
        );
    }
}
