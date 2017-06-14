package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import uk.gov.dwp.queue.triage.core.client.FailedMessage;
import uk.gov.dwp.queue.triage.core.client.FailedMessageId;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;

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
}
