package uk.gov.dwp.queue.triage.core.service.processor;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Predicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class PredicateOutcomeFailedMessageProcessorTest {

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private boolean duplicate;
    private final Predicate<FailedMessage> uniqueFailedMessagePredicate = failedMessage -> duplicate;
    private final FailedMessageProcessor newFailedMessageProcessor = mock(FailedMessageProcessor.class);
    private final FailedMessageProcessor duplicateFailedMessageProcessor = mock(FailedMessageProcessor.class);

    private final PredicateOutcomeFailedMessageProcessor underTest = new PredicateOutcomeFailedMessageProcessor(uniqueFailedMessagePredicate, duplicateFailedMessageProcessor, newFailedMessageProcessor);

    @Test
    public void messageIsADuplicate() {
        duplicate = true;

        underTest.process(failedMessage);

        verify(duplicateFailedMessageProcessor).process(failedMessage);
        verifyZeroInteractions(newFailedMessageProcessor);
    }

    @Test
    public void messageIsNotADuplicate() {
        duplicate = false;

        underTest.process(failedMessage);

        verify(newFailedMessageProcessor).process(failedMessage);
        verifyZeroInteractions(duplicateFailedMessageProcessor);
    }
}