package uk.gov.dwp.queue.triage.core.resend;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResendScheduledExecutorService {

    private final ScheduledExecutorService scheduledExecutorService;

    public ResendScheduledExecutorService(ScheduledExecutorService scheduledExecutorService,
                                          ResendFailedMessageService resendFailedMessageService,
                                          long initalDelay,
                                          long executionFrequency,
                                          TimeUnit timeUnit) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.scheduledExecutorService.scheduleAtFixedRate(
                resendFailedMessageService::resendMessages,
                initalDelay,
                executionFrequency,
                timeUnit
        );
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
    }
}
