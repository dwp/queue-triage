package uk.gov.dwp.queue.triage.core.classification;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class MessageClassifierTest {

    private FailedMessage failedMessage = mock(FailedMessage.class);
    private FailedMessagePredicate alwaysTrue = failedMessage -> true;
    private FailedMessagePredicate alwaysFalse = failedMessage -> false;
    private FailedMessageAction failedMessageAction = mock(FailedMessageAction.class);

    @Test
    public void actionIsExecutedIfThePredicateIsTrue() throws Exception {
        MessageClassifier underTest = MessageClassifier.when(alwaysTrue).then(failedMessageAction);

        underTest.accept(failedMessage);

        verify(failedMessageAction).accept(failedMessage);
    }

    @Test
    public void actionIsNotExecutedIfThePredicateIsFalse() {
        MessageClassifier underTest = MessageClassifier.when(alwaysFalse).then(failedMessageAction);

        underTest.accept(failedMessage);

        verifyZeroInteractions(failedMessageAction);
    }

}