package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;

public class MessageClassificationOutcomeTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock(answer = Answers.RETURNS_SELF)
    private Description<String> description;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private FailedMessageAction failedMessageAction;

    @Test
    public void constructMatchedOutcome() {
        final MessageClassificationOutcome underTest = MessageClassificationOutcome.matched(failedMessage, description, failedMessageAction);

        assertThat(underTest.isMatched(), is(true));
        assertThat(underTest.getDescription(), is(description));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(failedMessageAction));

        underTest.execute();

        verify(failedMessageAction).accept(failedMessage);
    }

    @Test
    public void constructUnmatchedOutcome() {
        final MessageClassificationOutcome underTest = MessageClassificationOutcome.notMatched(failedMessage, description);

        assertThat(underTest.isMatched(), is(false));
        assertThat(underTest.getDescription(), is(description));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(nullValue()));

        // Ensure that a NullPointerException is not thrown
        underTest.execute();
    }

}