package uk.gov.dwp.queue.triage.core.classification.server.executor;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.classification.server.MessageClassificationService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class MessageClassificationExecutorServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClassificationExecutorServiceTest.class);

    private final MessageClassificationService messageClassificationService = mock(MessageClassificationService.class);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private MessageClassificationExecutorService underTest = new MessageClassificationExecutorService(
            scheduledExecutorService,
            messageClassificationService,
            0,
            100,
            TimeUnit.MILLISECONDS
    );

    @Before
    public void setUp() {
        doAnswer(decrementCountdownLatch())
                .when(messageClassificationService)
                .classifyFailedMessages();
    }

    @After
    public void tearDown() throws Exception {
        LOGGER.debug("Shutting down the scheduledExecutorService");
        scheduledExecutorService.shutdownNow();
    }

    @Test
    public void jobExecutesSuccessfully() throws Exception {
        underTest.start();
        verifyMessageClassificationServiceExecutions(75);
    }


    @Test
    public void jobContinuesToExecuteIfExceptionIsThrown() throws InterruptedException {
        doAnswer(decrementCountdownLatchAndThrowException())
                .when(messageClassificationService)
                .classifyFailedMessages();

        underTest.start();

        verifyMessageClassificationServiceExecutions(75);
        verifyMessageClassificationServiceExecutions(120);
    }

    @Test
    public void jobCanBeExecutedOnDemand() throws InterruptedException {
        MessageClassificationExecutorService underTest = new MessageClassificationExecutorService(
                scheduledExecutorService,
                messageClassificationService,
                1,
                1,
                TimeUnit.HOURS
        );
        underTest.start();

        assertThat(countDownLatch.getCount(), is(1L));

        underTest.execute();

        verifyMessageClassificationServiceExecutions(75);
    }

    @Test
    public void executorCanBePausedAndResumed() throws InterruptedException {

        underTest.start();

        verifyMessageClassificationServiceExecutions(75);

        underTest.pause();
        assertThat(countDownLatch.getCount(), is(1L));

        underTest.start();
        verifyMessageClassificationServiceExecutions(75);
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

    private TypeSafeDiagnosingMatcher<ScheduledFuture<?>> done(boolean done) {
        return new TypeSafeDiagnosingMatcher<ScheduledFuture<?>>() {
            @Override
            protected boolean matchesSafely(ScheduledFuture<?> scheduledFuture, Description description) {
                boolean matches = scheduledFuture.isDone() == done;
                if (!matches) {
                    description.appendText("is").appendText(done ? "" : "not").appendText(" done");
                }
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is done");
            }
        };
    }

    private TypeSafeDiagnosingMatcher<ScheduledFuture<?>> cancelled(boolean cancelled) {
        return new TypeSafeDiagnosingMatcher<ScheduledFuture<?>>() {
            @Override
            protected boolean matchesSafely(ScheduledFuture<?> scheduledFuture, Description description) {
                boolean matches = scheduledFuture.isCancelled() == cancelled;
                if (!matches) {
                    description.appendText("is").appendText(cancelled ? "" : "not").appendText(" cancelled");
                }
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is cancelled");
            }
        };
    }

    private void verifyMessageClassificationServiceExecutions(int timeoutInMillis) throws InterruptedException {
        LOGGER.debug("Verifying messageClassificationService has been called within {}ms", timeoutInMillis);
        assertThat(countDownLatch.await(timeoutInMillis, TimeUnit.MILLISECONDS), is(true));
        LOGGER.debug("Verified messageClassificationService has been called");
        countDownLatch = new CountDownLatch(1);
    }
}