package uk.gov.dwp.queue.triage.core.classification.server.repository.memory;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup;

import java.util.Vector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static uk.gov.dwp.queue.triage.core.classification.classifier.UnmatchedMessageClassifier.ALWAYS_UNMATCHED;

public class InMemoryMessageClassificationRepositoryTest {

    private final Vector<MessageClassifier> messageClassifiers = new Vector<>();
    private final MessageClassifierGroup messageClassifier = mock(MessageClassifierGroup.class);

    private final InMemoryMessageClassificationRepository underTest = new InMemoryMessageClassificationRepository(messageClassifiers);

    @Test
    public void saveAndGetLatestMessageClassifier() {
        underTest.save(messageClassifier);

        assertThat(messageClassifiers, contains(messageClassifier));
        assertThat(underTest.findLatest(), is(messageClassifier));
    }

    @Test
    public void deleteAllMessageClassifiers() {
        messageClassifiers.add(messageClassifier);

        underTest.deleteAll();

        assertThat(messageClassifiers, contains(messageClassifier, ALWAYS_UNMATCHED));
    }

    @Test
    public void findLatestMessageClassifiers() {
        messageClassifiers.add(mock(MessageClassifier.class));
        messageClassifiers.add(messageClassifier);

        final MessageClassifier messageClassifiers = underTest.findLatest();

        assertThat(messageClassifiers, is(messageClassifier));
    }

    @Test
    public void findLatestMessageClassifierWhenEmpty() {
        assertThat(new InMemoryMessageClassificationRepository().findLatest(), nullValue());
    }
}