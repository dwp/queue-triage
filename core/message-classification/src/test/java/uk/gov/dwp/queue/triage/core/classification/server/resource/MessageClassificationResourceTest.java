package uk.gov.dwp.queue.triage.core.classification.server.resource;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageClassificationResourceTest {

    @Rule
    public MockitoRule mockitoJUnit = MockitoJUnit.rule();

    @Mock
    private MessageClassificationRepository repository;
    @Mock
    private MessageClassifier messageClassifier;
    @Mock
    private List<MessageClassifier> messageClassifiers;

    private MessageClassificationResource underTest;

    @Before
    public void setUp() {
        underTest = new MessageClassificationResource(repository);
    }

    @Test
    public void insertMessageClassifierDelegatesToRepository() throws Exception {
        underTest.addMessageClassifier(messageClassifier);

        verify(repository).insert(messageClassifier);
    }

    @Test
    public void listMessageClassifiers() throws Exception {
        when(repository.findAll()).thenReturn(messageClassifiers);

        assertThat(underTest.listAllMessageClassifiers(), Matchers.is(messageClassifiers));
    }

    @Test
    public void removeAllDelegatesFromRepository() throws Exception {
        underTest.removeAllMessageClassifiers();

        verify(repository).deleteAll();
    }
}