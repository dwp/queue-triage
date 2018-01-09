package uk.gov.dwp.queue.triage.core.classification.server;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;
import uk.gov.dwp.queue.triage.core.classification.server.repository.MessageClassificationRepository;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;

public class MessageClassificationServiceTest {

    private final FailedMessageSearchService failedMessageSearchService = mock(FailedMessageSearchService.class);
    private final MessageClassificationRepository messageClassificationRepository = mock(MessageClassificationRepository.class);

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final MessageClassifier messageClassifier1 = mock(MessageClassifier.class);
    private final MessageClassifier messageClassifier2 = mock(MessageClassifier.class);

    private final MessageClassificationService underTest = new MessageClassificationService(
            failedMessageSearchService,
            messageClassificationRepository
    );

    @Test
    public void noFailedMessagesExist() throws Exception {
        when(failedMessageSearchService.findByStatus(FAILED)).thenReturn(Collections.emptyList());

        underTest.classifyFailedMessages();

        verifyZeroInteractions(messageClassificationRepository);
    }

    @Test
    public void whenAClassifierIsNotApplicableTheActionIsNotExecuted() throws Exception {
        when(failedMessageSearchService.findByStatus(FAILED)).thenReturn(singletonList(failedMessage));
        when(messageClassificationRepository.findAll()).thenReturn(singletonList(messageClassifier1));
        when(messageClassifier1.test(failedMessage)).thenReturn(false);

        underTest.classifyFailedMessages();

        verify(messageClassifier1).test(failedMessage);
        verify(messageClassifier1, never()).accept(failedMessage);
    }

    @Test
    public void whenMultipleClassifiersAreApplicableOnlyTheFirstActionIsExecuted() throws Exception {
        when(failedMessageSearchService.findByStatus(FAILED)).thenReturn(singletonList(failedMessage));
        when(messageClassificationRepository.findAll()).thenReturn(Arrays.asList(messageClassifier1, messageClassifier2));
        when(messageClassifier1.test(failedMessage)).thenReturn(true);
        when(messageClassifier2.test(failedMessage)).thenReturn(true);

        underTest.classifyFailedMessages();

        verify(messageClassifier1).test(failedMessage);
        verify(messageClassifier1).accept(failedMessage);
        verifyZeroInteractions(messageClassifier2);
    }
}