package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

public class MessageClassificationContextTest {

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessagePredicate failedMessagePredicate = mock(FailedMessagePredicate.class);
    private final FailedMessageAction failedMessageAction = mock(FailedMessageAction.class);

    private final MessageClassificationContext underTest = new MessageClassificationContext(failedMessage);

    @Test
    public void matchedWithNoAction() {
        final MessageClassificationOutcome outcome = underTest.matched(failedMessagePredicate);

        assertThat(outcome.isMatched(), is(true));
        assertThat(outcome.getFailedMessage(), is(failedMessage));
        assertThat(outcome.getFailedMessageAction(), is(nullValue()));
        assertThat(outcome.getFailedMessagePredicate(), is(new FailedMessagePredicateWithResult(true, failedMessagePredicate)));
    }

    @Test
    public void matchedWithAction() {
        final MessageClassificationOutcome outcome = underTest.matched(failedMessagePredicate, failedMessageAction);

        assertThat(outcome.isMatched(), is(true));
        assertThat(outcome.getFailedMessage(), is(failedMessage));
        assertThat(outcome.getFailedMessageAction(), is(failedMessageAction));
        assertThat(outcome.getFailedMessagePredicate(), is(new FailedMessagePredicateWithResult(true, failedMessagePredicate)));
    }

    @Test
    public void notMatched() {
        final MessageClassificationOutcome outcome = underTest.notMatched(failedMessagePredicate);

        assertThat(outcome.isMatched(), is(false));
        assertThat(outcome.getFailedMessage(), is(failedMessage));
        assertThat(outcome.getFailedMessageAction(), is(nullValue()));
        assertThat(outcome.getFailedMessagePredicate(), is(new FailedMessagePredicateWithResult(false, failedMessagePredicate)));
    }
}