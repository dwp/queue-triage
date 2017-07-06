package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static uk.gov.dwp.queue.triage.core.dao.mongo.DestinationDBObjectConverter.BROKER_NAME;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;

public class FailedMessageMongoDao implements FailedMessageDao {

    private final DBCollection collection;
    private final FailedMessageConverter failedMessageConverter;

    public FailedMessageMongoDao(DBCollection collection, FailedMessageConverter failedMessageConverter) {
        this.collection = collection;
        this.failedMessageConverter = failedMessageConverter;
    }

    @Override
    public void insert(FailedMessage failedMessage) {
        collection.insert(failedMessageConverter.convertFromObject(failedMessage));
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
}
