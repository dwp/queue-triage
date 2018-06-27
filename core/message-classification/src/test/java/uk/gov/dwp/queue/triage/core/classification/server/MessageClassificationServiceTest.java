package uk.gov.dwp.queue.triage.core.classification.server;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassificationOutcome;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;

public class MessageClassificationServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock(answer = Answers.RETURNS_SELF)
    private Description<String> description;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private MessageClassificationOutcome<String> messageClassificationOutcome;
    @Mock
    private FailedMessageSearchService failedMessageSearchService;
    @Mock
    private MessageClassificationRepository messageClassificationRepository;
    @Mock
    private MessageClassifierGroup messageClassifier;

    private MessageClassificationService underTest;

    @Before
    public void setUp() {
        underTest = new MessageClassificationService<>(
                failedMessageSearchService,
                messageClassificationRepository,
                () -> description
        );
    }

    @Test
    public void classifyFailedMessagesWhenNoneExist() {
        when(failedMessageSearchService.findByStatus(FAILED)).thenReturn(Collections.emptyList());

        underTest.classifyFailedMessages();

        verifyZeroInteractions(messageClassificationRepository);
    }

    @Test
    public void classifyFailedMessagesNotMatched() {
        when(failedMessageSearchService.findByStatus(FAILED)).thenReturn(singletonList(failedMessage));
        when(messageClassificationRepository.findLatest()).thenReturn(messageClassifier);
        when(messageClassifier.classify(failedMessage, description)).thenReturn(messageClassificationOutcome);
        when(messageClassificationOutcome.isMatched()).thenReturn(false);

        underTest.classifyFailedMessages();

        verify(messageClassifier).classify(failedMessage, description);
        verify(messageClassificationOutcome).getDescription();
        verify(messageClassificationOutcome).execute();
    }

    @Test
    public void classifyFailedMessagesMatched() {
        when(failedMessageSearchService.findByStatus(FAILED)).thenReturn(singletonList(failedMessage));
        when(messageClassificationRepository.findLatest()).thenReturn(messageClassifier);
        when(messageClassifier.classify(failedMessage, description)).thenReturn(messageClassificationOutcome);

        underTest.classifyFailedMessages();

        verify(messageClassifier).classify(failedMessage, description);
        verify(messageClassificationOutcome).getDescription();
        verify(messageClassificationOutcome).execute();
    }
}