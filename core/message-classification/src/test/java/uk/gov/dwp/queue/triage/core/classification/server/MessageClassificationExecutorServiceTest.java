package uk.gov.dwp.queue.triage.core.classification.server;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.Timeout;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessageClassificationExecutorServiceTest {

    private final MessageClassificationService messageClassificationService = mock(MessageClassificationService.class);

    private MessageClassificationExecutorService underTest = new MessageClassificationExecutorService(
            Executors.newSingleThreadScheduledExecutor(),
            messageClassificationService,
            0,
            50,
            TimeUnit.MILLISECONDS
    );

    @Test
    public void jobExecutesSuccessfully() throws Exception {
        underTest.start();
        verify(messageClassificationService, times(1, timeout(25))).classifyFailedMessages();
        underTest.stop();
    }

    @Test
    public void jobContinuesToExecuteIfExceptionIsThrown() {
        doThrow(new RuntimeException()).when(messageClassificationService).classifyFailedMessages();

        underTest.start();

        verify(messageClassificationService, times(2, timeout(75))).classifyFailedMessages();
    }

    @Test
    public void jobCanBeExecutedOnDemand() {
        underTest = new MessageClassificationExecutorService(
                Executors.newSingleThreadScheduledExecutor(),
                messageClassificationService,
                1,
                1,
                TimeUnit.HOURS
        );
        underTest.start();

        verify(messageClassificationService, times(0, timeout(50))).classifyFailedMessages();
        underTest.execute();
        verify(messageClassificationService, times(1, timeout(50))).classifyFailedMessages();
        underTest.stop();
    }

    @Test
    public void executorCanBePausedAndResumed() {
        underTest.start();
        verify(messageClassificationService, times(1, timeout(25))).classifyFailedMessages();
        underTest.pause();
        verify(messageClassificationService, times(1, timeout(75))).classifyFailedMessages();
        underTest.start();
        verify(messageClassificationService, times(2, timeout(25))).classifyFailedMessages();
        underTest.stop();
    }

    private Timeout times(int executions, long millis) {
        return new Timeout(millis, Mockito.times(executions));
    }

    private long timeout(long millis) {
        return millis;
    }
}