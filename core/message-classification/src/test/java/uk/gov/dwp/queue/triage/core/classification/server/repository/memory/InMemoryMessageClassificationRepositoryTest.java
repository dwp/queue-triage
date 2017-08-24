package uk.gov.dwp.queue.triage.core.classification.server.repository.memory;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class InMemoryMessageClassificationRepositoryTest {

    private final ArrayList<MessageClassifier> messageClassifiers = new ArrayList<>();
    private final MessageClassifier messageClassifier = mock(MessageClassifier.class);

    private final InMemoryMessageClassificationRepository underTest =
            new InMemoryMessageClassificationRepository(messageClassifiers);

    @Test
    public void insertAMessageClassifier() {
        underTest.insert(messageClassifier);
        assertThat(messageClassifiers, contains(messageClassifier));
    }

    @Test
    public void deleteAllMessageClassifiers() throws Exception {
        messageClassifiers.add(messageClassifier);

        underTest.deleteAll();

        assertThat(messageClassifiers, is(empty()));
    }

    @Test
    public void findAllMessageClassifiers() throws Exception {
        messageClassifiers.add(messageClassifier);

        assertThat(underTest.findAll(), contains(messageClassifier));
    }
}