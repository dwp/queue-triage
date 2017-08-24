package uk.gov.dwp.queue.triage.core.classification.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Collection;
import java.util.List;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.FAILED;

public class MessageClassificationService {

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
        if (failedMessages.size() == 0) {
            LOGGER.info("No messages require classification");
            return;
        }
        LOGGER.info("Attempting to classify {} messages", failedMessages.size());
        List<MessageClassifier> messageClassifiers = messageClassificationRepository.findAll();

        // This is VERY inefficient, think of a better implementation for example
        // split the classifiers by broker and/or queue name
        failedMessages
                .forEach(failedMessage -> messageClassifiers
                        .stream()
                        .filter(messageClassifier -> messageClassifier.test(failedMessage))
                        .peek(messageClassifier -> LOGGER.debug("Applying MessageClassifier to FailedMessageId: {}", failedMessage.getFailedMessageId()))
                        .findFirst()
                        .ifPresent(messageClassifier -> messageClassifier.accept(failedMessage)));
    }
}
