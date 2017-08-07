package uk.gov.dwp.queue.triage.core.classification;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        assertThat(new MessageClassifier(alwaysTrue, failedMessageAction).classify(failedMessage), is(true));

        verify(failedMessageAction).accept(failedMessage);
    }

    @Test
    public void actionIsNotExecutedIfThePredicateIsFalse() {
        assertThat(new MessageClassifier(alwaysFalse, failedMessageAction).classify(failedMessage), is(false));

        verifyZeroInteractions(failedMessageAction);
    }

}