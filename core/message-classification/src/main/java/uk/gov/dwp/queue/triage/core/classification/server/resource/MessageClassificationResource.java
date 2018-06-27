package uk.gov.dwp.queue.triage.core.classification.server.resource;

import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcomeAdapter;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationClient;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationOutcomeResponse;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.function.Supplier;

import static uk.gov.dwp.queue.triage.core.domain.FailedMessageNotFoundException.failedMessageNotFound;

public class MessageClassificationResource<T> implements MessageClassificationClient {

    private final MessageClassificationRepository messageClassificationRepository;
    private final FailedMessageSearchService failedMessageSearchService;
    private final Supplier<Description<T>> descriptionFactory;
    private final MessageClassificationOutcomeAdapter outcomeAdapter;

    public MessageClassificationResource(MessageClassificationRepository messageClassificationRepository,
                                         FailedMessageSearchService failedMessageSearchService,
                                         Supplier<Description<T>> descriptionFactory,
                                         MessageClassificationOutcomeAdapter outcomeAdapter) {
        this.messageClassificationRepository = messageClassificationRepository;
        this.failedMessageSearchService = failedMessageSearchService;
        this.descriptionFactory = descriptionFactory;
        this.outcomeAdapter = outcomeAdapter;
    }

    @Override
    public void addMessageClassifier(MessageClassifier messageClassifier) {
        messageClassificationRepository.save(MessageClassifierGroup.newClassifierCollection().withClassifier(messageClassifier).build());
    }

    @Override
    public MessageClassifier listAllMessageClassifiers() {
        return messageClassificationRepository.findLatest();
    }

    @Override
    public void removeAllMessageClassifiers() {
        messageClassificationRepository.deleteAll();
    }

    @Override
    public MessageClassificationOutcomeResponse classifyFailedMessage(FailedMessageId failedMessageId) {
        return failedMessageSearchService.findById(failedMessageId)
                .map(this::classifyFailedMessage)
                .orElseThrow(failedMessageNotFound(failedMessageId));
    }

    @Override
    public MessageClassificationOutcomeResponse classifyFailedMessage(FailedMessage failedMessage) {
        return outcomeAdapter.toOutcomeResponse(messageClassificationRepository
                .findLatest()
                .classify(failedMessage, descriptionFactory.get()));
    }
}
