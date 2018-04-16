package uk.gov.dwp.queue.triage.core.search.mongo;

import org.bson.Document;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.newSearchFailedMessageResponse;

public class MongoSearchResponseAdapter {

    private final FailedMessageConverter failedMessageConverter;

    public MongoSearchResponseAdapter(FailedMessageConverter failedMessageConverter) {
        this.failedMessageConverter = failedMessageConverter;
    }

    public SearchFailedMessageResponse toResponse(Document document) {
        Destination destination = failedMessageConverter.getDestination(document);
        return newSearchFailedMessageResponse()
                .withFailedMessageId(failedMessageConverter.getFailedMessageId(document))
                .withBroker(destination.getBrokerName())
                .withDestination(destination.getName().orElse(null))
                .withContent(failedMessageConverter.getContent(document))
                .withSentDateTime(failedMessageConverter.getSentDateTime(document))
                .withFailedDateTime(failedMessageConverter.getFailedDateTime(document))
                .build();

    }
}
