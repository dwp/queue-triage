package uk.gov.dwp.queue.triage.core.resend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ResendScheduledExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendScheduledExecutorService.class);

    private final ScheduledExecutorService scheduledExecutorService;
    private final Runnable runnable;
    private final long initialDelay;
    private final long executionFrequency;
    private final TimeUnit timeUnit;
    private final String brokerName;

    private ScheduledFuture<?> futureTask;

    public ResendScheduledExecutorService(ScheduledExecutorService scheduledExecutorService,
                                          ResendFailedMessageService resendFailedMessageService,
                                          long initialDelay,
                                          long executionFrequency,
                                          TimeUnit timeUnit) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.brokerName = resendFailedMessageService.getBrokerName();
        this.runnable = () -> {
            try {
                LOGGER.info("Executing the resend FailedMessages job for broker: {}", brokerName);
                resendFailedMessageService.resendMessages();
            } catch (Throwable t) {
                LOGGER.error("An error occurred resending FailedMessages for broker: " + brokerName, t);
            }
        };
        this.initialDelay = initialDelay;
        this.executionFrequency = executionFrequency;
        this.timeUnit = timeUnit;
    }

    public void start() {
        LOGGER.info("ResendFailedMessageService scheduled to start in {} {} and then execute every {} {}",
                initialDelay,
                timeUnit,
                executionFrequency,
                timeUnit
        );
        scheduleAtAFixedRate(initialDelay);
    }

    public void execute() {
        cancelFutureTask();
        LOGGER.info("Executing the ResendFailedMessageService immediately and then scheduling to execute every {} {}",
                executionFrequency,
                timeUnit
        );
        scheduleAtAFixedRate(0);
    }

    public void pause() {
        LOGGER.info("Pausing execution of the ResendFailedMessageService");
        cancelFutureTask();
        LOGGER.info("Execution of the ResendFailedMessageService paused");
    }

    private void cancelFutureTask() {
        if (futureTask != null && !futureTask.isCancelled()) {
            futureTask.cancel(true);
        }
    }

    public void stop() {
        LOGGER.info("Stopping execution of the ResendFailedMessageService");
        scheduledExecutorService.shutdown();
        LOGGER.info("Execution of the ResendFailedMessageService stopped");
    }

    public String getBrokerName() {
        return brokerName;
    }

    private void scheduleAtAFixedRate(long initialDelay) {
        futureTask = this.scheduledExecutorService.scheduleAtFixedRate(
                runnable,
                initialDelay,
                executionFrequency,
                timeUnit
        );
    }
}
