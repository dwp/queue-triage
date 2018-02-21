package uk.gov.dwp.queue.triage.core.resend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.executor.AbstractScheduledExecutorService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResendScheduledExecutorService extends AbstractScheduledExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendScheduledExecutorService.class);

    public ResendScheduledExecutorService(ScheduledExecutorService scheduledExecutorService,
                                          ResendFailedMessageService resendFailedMessageService,
                                          long initialDelay,
                                          long executionFrequency,
                                          TimeUnit timeUnit) {
        super(scheduledExecutorService,
                resendFailedMessageService.getBrokerName(),
                () -> {
                    try {
                        LOGGER.debug("Executing the resend FailedMessages job for broker: {}", resendFailedMessageService.getBrokerName());
                        resendFailedMessageService.resendMessages();
                    } catch (Throwable t) {
                        LOGGER.error("An error occurred resending FailedMessages for broker: {}", resendFailedMessageService.getBrokerName(), t);
                    }
                },
                initialDelay, executionFrequency, timeUnit);
    }

    @Override
    public String getServiceName() {
        return "ResendFailedMessageService";
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
