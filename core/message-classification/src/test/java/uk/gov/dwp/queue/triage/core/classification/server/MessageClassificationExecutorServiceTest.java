package uk.gov.dwp.queue.triage.core.classification.server;

import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class MessageClassificationExecutorServiceTest {

    private final MessageClassificationService messageClassificationService = mock(MessageClassificationService.class);

    private MessageClassificationExecutorService underTest = new MessageClassificationExecutorService(
            Executors.newSingleThreadScheduledExecutor(),
            messageClassificationService,
            0,
            100,
            TimeUnit.MILLISECONDS
    );

    @Test
    public void jobExecutesSuccessfully() throws Exception {
        underTest.start();
        verify(messageClassificationService, timeout(75).times(1)).classifyFailedMessages();
        underTest.stop();
    }

    @Test
    public void jobContinuesToExecuteIfExceptionIsThrown() {
        doThrow(new RuntimeException()).when(messageClassificationService).classifyFailedMessages();

        underTest.start();

        verify(messageClassificationService, timeout(175).times(2)).classifyFailedMessages();

        underTest.stop();
    }

    @Test
    public void jobCanBeExecutedOnDemand() {
        MessageClassificationExecutorService underTest = new MessageClassificationExecutorService(
                Executors.newSingleThreadScheduledExecutor(),
                messageClassificationService,
                1,
                1,
                TimeUnit.HOURS
        );
        underTest.start();

        verify(messageClassificationService, timeout(50).times(0)).classifyFailedMessages();
        underTest.execute();
        verify(messageClassificationService, timeout(150).times(1)).classifyFailedMessages();
        underTest.stop();
    }

    @Test
    public void executorCanBePausedAndResumed() {
        underTest.start();
        verify(messageClassificationService, timeout(75).times(1)).classifyFailedMessages();
        underTest.pause();
        verify(messageClassificationService, timeout(75).times(1)).classifyFailedMessages();
        underTest.start();
        verify(messageClassificationService, timeout(75).times(2)).classifyFailedMessages();
        underTest.stop();
    }
}