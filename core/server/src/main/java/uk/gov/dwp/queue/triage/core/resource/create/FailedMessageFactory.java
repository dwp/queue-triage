package uk.gov.dwp.queue.triage.core.resource.create;

import uk.gov.dwp.queue.triage.core.client.CreateFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

public class FailedMessageFactory {

        public FailedMessage create(CreateFailedMessageRequest request) {
            return FailedMessageBuilder.newFailedMessage()
                    .withFailedMessageId(request.getFailedMessageId())
                    .withContent(request.getContent())
                    .withDestination(new Destination(request.getBrokerName(), request.getDestinationName()))
                    .withFailedDateTime(request.getFailedAt())
                    .withProperties(request.getProperties())
                    .withSentDateTime(request.getSentAt())
                    .build();
        }
    }