package uk.gov.dwp.queue.triage.core.jms.activemq.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.jms.activemq.MessageListenerManager;
import uk.gov.dwp.queue.triage.executor.AbstractScheduledExecutorService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueBrowserScheduledExecutorService extends AbstractScheduledExecutorService implements MessageListenerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueBrowserScheduledExecutorService.class);

    public QueueBrowserScheduledExecutorService(ScheduledExecutorService scheduledExecutorService,
                                                QueueBrowserService queueBrowserService,
                                                long initialDelay,
                                                long executionFrequency,
                                                TimeUnit timeUnit) {
        super(scheduledExecutorService,
                queueBrowserService.getBrokerName(),
                () -> {
            try {
                LOGGER.debug("Browsing FailedMessages on broker: {}", queueBrowserService.getBrokerName());
                queueBrowserService.browse();
            } catch (Throwable t) {
                LOGGER.error("An error occurred browsing FailedMessages on broker: {}", queueBrowserService.getBrokerName(), t);
            }
        }, initialDelay, executionFrequency, timeUnit);
    }

    @Override
    protected String getServiceName() {
        return "QueueBrowserService";
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void stop() {
        pause();
    }
}
