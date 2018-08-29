package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.predicate.BooleanPredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.newClassifierCollection;

public class UnmatchedMessageClassifierTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private MessageClassificationContext context;
    @Mock
    private MessageClassificationOutcome outcome;
    @Mock
    private FailedMessage failedMessage;

    private final MessageClassifier underTest = UnmatchedMessageClassifier.ALWAYS_UNMATCHED;

    @Test
    public void testClassify() {
        when(context.getFailedMessage()).thenReturn(failedMessage);
        when(context.notMatched(new BooleanPredicate(false))).thenReturn(outcome);

        assertThat(underTest.classify(context), is(outcome));
    }

    @Test
    public void testToString() {
        assertThat(underTest.toString(), is("unmatched"));
    }

    @Test
    public void testEquals() {
        assertThat(underTest.equals(UnmatchedMessageClassifier.ALWAYS_UNMATCHED), is(true));
        assertThat(underTest.equals(newClassifierCollection().build()), is(false));
        assertThat(underTest.equals(null), is(false));
    }

    @Test
    public void testHashCode() {
        assertThat(underTest.hashCode(), is(544));
    }
}