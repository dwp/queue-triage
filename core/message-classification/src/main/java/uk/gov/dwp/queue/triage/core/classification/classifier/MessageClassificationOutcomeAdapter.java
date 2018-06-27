package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationOutcomeResponse;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseFactory;

public class MessageClassificationOutcomeAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClassificationOutcomeAdapter.class);

    private final ObjectMapper objectMapper;
    private final FailedMessageResponseFactory failedMessageResponseFactory;

    public MessageClassificationOutcomeAdapter(ObjectMapper objectMapper,
                                               FailedMessageResponseFactory failedMessageResponseFactory) {
        this.objectMapper = objectMapper;
        this.failedMessageResponseFactory = failedMessageResponseFactory;
    }

    public <T> MessageClassificationOutcomeResponse toOutcomeResponse(MessageClassificationOutcome<T> outcome) {
        String failedMessageAction;
        try {
            failedMessageAction = objectMapper.writeValueAsString(outcome.getFailedMessageAction());
        } catch (JsonProcessingException e) {
            failedMessageAction = outcome.getFailedMessageAction().getClass().getSimpleName();
            LOGGER.error("Could not serialise {}", failedMessageAction, e);
        }
        return new MessageClassificationOutcomeResponse(
                outcome.isMatched(),
                outcome.getDescription().getOutput().toString(),
                failedMessageResponseFactory.create(outcome.getFailedMessage()),
                failedMessageAction
        );
    }
}
