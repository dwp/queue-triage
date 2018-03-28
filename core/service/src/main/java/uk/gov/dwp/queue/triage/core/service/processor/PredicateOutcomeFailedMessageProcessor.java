package uk.gov.dwp.queue.triage.core.service.processor;

import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Predicate;

/**
 * Based on if the FailedMessage is deemed unique, handles the message in one of two ways
 */
public class PredicateOutcomeFailedMessageProcessor implements FailedMessageProcessor {

    private final Predicate<FailedMessage> predicate;
    private final FailedMessageProcessor positiveOutcomeFailedMessageProcessor;
    private final FailedMessageProcessor negativeOutcomeFailedMessageProcessor;

    public PredicateOutcomeFailedMessageProcessor(Predicate<FailedMessage> predicate,
                                                  FailedMessageProcessor positiveOutcomeFailedMessageProcessor,
                                                  FailedMessageProcessor negativeOutcomeFailedMessageProcessor) {
        this.predicate = predicate;
        this.negativeOutcomeFailedMessageProcessor = negativeOutcomeFailedMessageProcessor;
        this.positiveOutcomeFailedMessageProcessor = positiveOutcomeFailedMessageProcessor;
    }

    @Override
    public void process(FailedMessage failedMessage) {
        if (predicate.test(failedMessage)) {
            positiveOutcomeFailedMessageProcessor.process(failedMessage);
        } else {
            negativeOutcomeFailedMessageProcessor.process(failedMessage);
        }
    }
}
