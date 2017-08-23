package uk.gov.dwp.queue.triage.core.classification.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageClassificationExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageClassificationExecutorService.class);

    private final ScheduledExecutorService scheduledExecutorService;
    private final MessageClassificationService messageClassificationService;
    private final long initialDelay;
    private final long executionFrequency;
    private final TimeUnit timeUnit;

    public MessageClassificationExecutorService(ScheduledExecutorService scheduledExecutorService,
                                                MessageClassificationService messageClassificationService,
                                                long initialDelay,
                                                long executionFrequency,
                                                TimeUnit timeUnit) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.messageClassificationService = messageClassificationService;
        this.initialDelay = initialDelay;
        this.executionFrequency = executionFrequency;
        this.timeUnit = timeUnit;
    }

    public void start() {
        LOGGER.info("Scheduling the MessageClassificationService to execute every {} {}", executionFrequency, timeUnit);
        this.scheduledExecutorService.scheduleAtFixedRate(
                messageClassificationService::classifyFailedMessages,
                initialDelay,
                executionFrequency,
                timeUnit
        );
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
