package uk.gov.dwp.queue.triage.core.classification.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationContext;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcome;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Collection;

import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;

public class MessageClassificationService<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClassificationService.class);

    private final FailedMessageSearchService failedMessageSearchService;
    private final MessageClassificationRepository messageClassificationRepository;

    public MessageClassificationService(FailedMessageSearchService failedMessageSearchService,
                                        MessageClassificationRepository messageClassificationRepository) {
        this.failedMessageSearchService = failedMessageSearchService;
        this.messageClassificationRepository = messageClassificationRepository;
    }

    public void classifyFailedMessages() {
        Collection<FailedMessage> failedMessages = failedMessageSearchService.findByStatus(FAILED);
        if (failedMessages.isEmpty()) {
            LOGGER.info("No messages require classification");
            return;
        }
        LOGGER.info("Attempting to classify {} messages", failedMessages.size());
        MessageClassifier messageClassifiers = messageClassificationRepository.findLatest();

        failedMessages
                .stream()
                .map(failedMessage -> messageClassifiers.classify(new MessageClassificationContext(failedMessage)))
                .peek(outcome -> LOGGER.debug("{}", outcome.getDescription()))
                .forEach(MessageClassificationOutcome::execute);
    }
}
