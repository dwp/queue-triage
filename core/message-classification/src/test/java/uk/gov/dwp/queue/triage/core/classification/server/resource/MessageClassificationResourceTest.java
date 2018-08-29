package uk.gov.dwp.queue.triage.core.classification.server.resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationContext;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcome;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcomeAdapter;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationOutcomeResponse;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageNotFoundException;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MessageClassificationResourceTest {

    private final FailedMessageId failedMessageId = FailedMessageId.newFailedMessageId();

    @Rule
    public MockitoRule mockitoJUnit = MockitoJUnit.rule();

    @Mock
    private MessageClassificationRepository repository;
    @Mock
    private FailedMessageSearchService failedMessageSearchService;
    @Mock
    private MessageClassifierGroup messageClassifier;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private MessageClassificationOutcome messageClassificationOutcome;
    @Mock
    private MessageClassificationOutcomeAdapter outcomeAdapter;
    @Mock
    private MessageClassificationOutcomeResponse messageClassificationOutcomeResponse;
    private MessageClassificationResource underTest;

    @Before
    public void setUp() {
        underTest = new MessageClassificationResource<>(repository, failedMessageSearchService, outcomeAdapter);
    }

    @Test
    public void addMessageClassifierDelegatesToRepository() {
        ArgumentCaptor<MessageClassifierGroup> captor = ArgumentCaptor.forClass(MessageClassifierGroup.class);
        Mockito.doNothing().when(repository).save(captor.capture());

        underTest.addMessageClassifier(messageClassifier);

        assertThat(captor.getValue().getClassifiers(), contains(messageClassifier));
        verify(repository).save(captor.getValue());
    }

    @Test
    public void listMessageClassifiers() {
        when(repository.findLatest()).thenReturn(messageClassifier);

        assertThat(underTest.listAllMessageClassifiers(), is(messageClassifier));
    }

    @Test
    public void removeAllDelegatesFromRepository() {
        underTest.removeAllMessageClassifiers();

        verify(repository).deleteAll();
    }

    @Test(expected = FailedMessageNotFoundException.class)
    public void classifyByIfWhenFailedMessageDoesNotExist() {
        when(failedMessageSearchService.findById(failedMessageId)).thenReturn(Optional.empty());

        underTest.classifyFailedMessage(failedMessageId);

        verifyZeroInteractions(messageClassifier);
        verifyZeroInteractions(outcomeAdapter);
    }

    @Test
    public void classifyByFailedMessageId() {
        when(failedMessageSearchService.findById(failedMessageId)).thenReturn(Optional.of(failedMessage));
        when(repository.findLatest()).thenReturn(messageClassifier);
        when(messageClassifier.classify(new MessageClassificationContext(failedMessage))).thenReturn(messageClassificationOutcome);
        when(outcomeAdapter.toOutcomeResponse(messageClassificationOutcome)).thenReturn(messageClassificationOutcomeResponse);

        final MessageClassificationOutcomeResponse outcomeResponse = underTest.classifyFailedMessage(failedMessageId);

        assertThat(outcomeResponse, is(messageClassificationOutcomeResponse));
        verify(failedMessageSearchService).findById(failedMessageId);
        verify(messageClassifier).classify(new MessageClassificationContext(failedMessage));
        verify(outcomeAdapter).toOutcomeResponse(messageClassificationOutcome);
        verifyZeroInteractions(messageClassificationOutcome);
    }

    @Test
    public void classifyFailedMessage() {
        when(repository.findLatest()).thenReturn(messageClassifier);
        when(messageClassifier.classify(new MessageClassificationContext(failedMessage))).thenReturn(messageClassificationOutcome);
        when(outcomeAdapter.toOutcomeResponse(messageClassificationOutcome)).thenReturn(messageClassificationOutcomeResponse);

        final MessageClassificationOutcomeResponse outcome = underTest.classifyFailedMessage(failedMessage);

        assertThat(outcome, is(messageClassificationOutcomeResponse));
        verify(messageClassifier).classify(new MessageClassificationContext(failedMessage));
        verify(outcomeAdapter).toOutcomeResponse(messageClassificationOutcome);
        verifyZeroInteractions(messageClassificationOutcome);
    }
}