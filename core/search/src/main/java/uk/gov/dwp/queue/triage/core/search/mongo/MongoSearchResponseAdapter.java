package uk.gov.dwp.queue.triage.core.search.mongo;

import com.mongodb.BasicDBObject;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;

public class MongoSearchResponseAdapter {

    private final FailedMessageConverter failedMessageConverter;

    public MongoSearchResponseAdapter(FailedMessageConverter failedMessageConverter) {
        this.failedMessageConverter = failedMessageConverter;
    }

    public SearchFailedMessageResponse toResponse(BasicDBObject basicDBObject) {
        Destination destination = failedMessageConverter.getDestination(basicDBObject);
        return newSearchFailedMessageResponse()
                .withFailedMessageId(failedMessageConverter.getFailedMessageId(basicDBObject))
                .withBroker(destination.getBrokerName())
                .withDestination(destination.getName().orElse(null))
                .withContent(failedMessageConverter.getContent(basicDBObject))
                .withSentDateTime(failedMessageConverter.getSentDateTime(basicDBObject))
                .withFailedDateTime(failedMessageConverter.getFailedDateTime(basicDBObject))
                .build();

    }
}
