package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.operation.OrderBy;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;

public class FailedMessageMongoDao implements FailedMessageDao {

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
        collection.insert(failedMessageConverter.convertFromObject(failedMessage));
    }

    @Override
    public void updateStatus(FailedMessageId failedMessageId, FailedMessageStatus failedMessageStatus) {
        collection.update(
                failedMessageConverter.createId(failedMessageId),
                new BasicDBObject("$push", new BasicDBObject(STATUS_HISTORY, new BasicDBObject()
                        .append("$each", singletonList(failedMessageStatusConverter.convertFromObject(failedMessageStatus)))
                        .append("$sort", new BasicDBObject("updatedDateTime", OrderBy.DESC.getIntRepresentation()))))
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
        return collection.remove(removeRecordsQueryFactory.create()).getN();
    }
}
