package uk.gov.dwp.queue.triage.core.jms.activemq.browser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class QueueBrowserScheduledExecutorServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueBrowserScheduledExecutorServiceTest.class);

    private final QueueBrowserService queueBrowserService = mock(QueueBrowserService.class);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private QueueBrowserScheduledExecutorService underTest = new QueueBrowserScheduledExecutorService(
            scheduledExecutorService,
            queueBrowserService,
            0,
            100,
            TimeUnit.MILLISECONDS
    );

    @Before
    public void setUp() {
        doAnswer(decrementCountdownLatch())
                .when(queueBrowserService)
                .browse();
    }

    @After
    public void tearDown() throws Exception {
        LOGGER.debug("Shutting down the scheduledExecutorService");
        scheduledExecutorService.shutdownNow();
    }

    @Test
    public void jobExecutesSuccessfully() throws Exception {
        underTest.start();
        verifyQueueBrowserServiceExecutions(75);
    }


    @Test
    public void jobContinuesToExecuteIfExceptionIsThrown() throws InterruptedException {
        doAnswer(decrementCountdownLatchAndThrowException())
                .when(queueBrowserService)
                .browse();

        underTest.start();

        verifyQueueBrowserServiceExecutions(75);
        verifyQueueBrowserServiceExecutions(120);
    }

    @Test
    public void jobCanBeExecutedOnDemand() throws InterruptedException {
        QueueBrowserScheduledExecutorService underTest = new QueueBrowserScheduledExecutorService(
                scheduledExecutorService,
                queueBrowserService,
                1,
                1,
                TimeUnit.HOURS
        );
        underTest.start();

        assertThat(countDownLatch.getCount(), is(1L));

        underTest.execute();

        verifyQueueBrowserServiceExecutions(0);
        assertThat(underTest.isPaused(), is(false));
    }

    @Test
    public void executorCanBePausedAndResumed() throws InterruptedException {

        underTest.start();

        verifyQueueBrowserServiceExecutions(75);

        underTest.pause();
        assertThat(countDownLatch.getCount(), is(1L));
        assertThat(underTest.isPaused(), is(true));

        underTest.start();
        verifyQueueBrowserServiceExecutions(75);
        assertThat(underTest.isPaused(), is(false));
    }

    @Test
    public void executorRemainsPausedIfExecuted() throws InterruptedException {
        underTest.start();
        verifyQueueBrowserServiceExecutions(75);
        assertThat(underTest.isPaused(), is(false));

        underTest.pause();
        assertThat(countDownLatch.getCount(), is(1L));
        assertThat(underTest.isPaused(), is(true));

        underTest.execute();
        verifyQueueBrowserServiceExecutions(0);
        assertThat(underTest.isPaused(), is(true));
    }

    private Answer decrementCountdownLatch() {
        return invocationOnMock -> {
            countDownLatch.countDown();
            return null;
        };
    }

    private Answer decrementCountdownLatchAndThrowException() {
        return invocationOnMock -> {
            countDownLatch.countDown();
            throw new RuntimeException("Head Shot!");
        };
    }

    private void verifyQueueBrowserServiceExecutions(int timeoutInMillis) throws InterruptedException {
        LOGGER.debug("Verifying queueBrowserService has been called within {}ms", timeoutInMillis);
        assertThat(countDownLatch.await(timeoutInMillis, TimeUnit.MILLISECONDS), is(true));
        LOGGER.debug("Verified queueBrowserService has been called");
        countDownLatch = new CountDownLatch(1);
    }
}