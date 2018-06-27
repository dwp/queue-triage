package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.newClassifierCollection;

public class UnmatchedMessageClassifierTest {

    private static final String DESCRIPTION = "always unmatched";
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private Description<String> description;

    private final MessageClassifier underTest = UnmatchedMessageClassifier.ALWAYS_UNMATCHED;

    @Test
    public void testClassify() {

        final MessageClassificationOutcome<String> outcome = underTest.classify(failedMessage, description);

        verify(description).append(DESCRIPTION);
        assertThat(outcome.isMatched(), is(false));
        assertThat(outcome.getFailedMessage(), is(failedMessage));
        assertThat(outcome.getDescription(), is(description));
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