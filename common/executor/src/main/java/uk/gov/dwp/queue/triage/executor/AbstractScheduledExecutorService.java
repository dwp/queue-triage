package uk.gov.dwp.queue.triage.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledExecutorService implements BrokerExecutorService {

    private final ScheduledExecutorService scheduledExecutorService;
    private final Runnable runnableService;
    private final long initialDelay;
    private final long executionFrequency;
    private final TimeUnit timeUnit;
    private final String brokerName;

    private RunnableScheduledFuture<?> futureTask;

    public AbstractScheduledExecutorService(ScheduledExecutorService scheduledExecutorService,
                                            String brokerName,
                                            Runnable runnableService,
                                            long initialDelay,
                                            long executionFrequency,
                                            TimeUnit timeUnit) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.brokerName = brokerName;
        this.runnableService = runnableService;
        this.initialDelay = initialDelay;
        this.executionFrequency = executionFrequency;
        this.timeUnit = timeUnit;
    }
    
    protected abstract String getServiceName();
    
    protected Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    @Override
    public void start() {
        getLogger().info("{} scheduled to start in {} {} and then execute every {} {}",
                getServiceName(),
                initialDelay,
                timeUnit,
                executionFrequency,
                timeUnit
        );
        scheduleAtAFixedRate(initialDelay);
    }

    @Override
    public void execute() {
        if (futureTask == null || futureTask.isCancelled()) {
            getLogger().info("Executing the {} immediately", getServiceName());
            runnableService.run();
        } else {
            getLogger().info("Executing the {} immediately, then scheduling to execute every {} {}",
                    getServiceName(),
                    executionFrequency,
                    timeUnit);
            futureTask.run();
        }
        getLogger().info("{} has executed successfully", getServiceName());
    }

    @Override
    public void pause() {
        getLogger().info("Pausing execution of the {}", getServiceName());
        cancelFutureTask();
        getLogger().info("Execution of the {} paused", getServiceName());
    }

    private void cancelFutureTask() {
        if (futureTask != null && !futureTask.isCancelled()) {
            futureTask.cancel(true);
        }
    }

    @Override
    public void stop() {
        getLogger().info("Stopping execution of the {}", getServiceName());
        scheduledExecutorService.shutdown();
        getLogger().info("Execution of the {} stopped", getServiceName());
    }

    @Override
    public String getBrokerName() {
        return brokerName;
    }

    @Override
    public boolean isPaused() {
        return futureTask.isCancelled();
    }

    private RunnableScheduledFuture<?> scheduleAtAFixedRate(long initialDelay) {
        futureTask = (RunnableScheduledFuture)scheduledExecutorService.scheduleAtFixedRate(
                runnableService,
                initialDelay,
                executionFrequency,
                timeUnit
        );
        return futureTask;
    }
}
