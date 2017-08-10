package uk.gov.dwp.queue.triage.core.configuration;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.remove.RemoveFailedMessageService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class RemoveFailedMessageConfiguration {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RemoveFailedMessageConfiguration.class);

    @Bean
    public RemoveFailedMessageService removeFailedMessageService(FailedMessageDao failedMessageDao) {
        return new RemoveFailedMessageService(failedMessageDao);
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService removeFailedMessagesScheduledExecutor(RemoveFailedMessageService removeFailedMessageService) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // Run every day at 05:00
        // TODO: Make this configurable via application.yml
        ZonedDateTime initialStartTime = LocalDate.now().plusDays(1).atStartOfDay().plusHours(5).atZone(ZoneId.systemDefault());
        long delay = ZonedDateTime.now().until(initialStartTime, ChronoUnit.MINUTES);
        LOGGER.info("Remove FailedMessage Job will start at: {} (in {} minutes)", initialStartTime, delay);
        scheduledExecutorService.scheduleAtFixedRate(
                removeFailedMessageService::removeFailedMessages,
                delay,
                TimeUnit.DAYS.toMinutes(1),
                TimeUnit.MINUTES
        );
        return scheduledExecutorService;
    }
}
