package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

@Configuration
public class FailedMessageServiceConfiguration {

    @Bean
    public FailedMessageService failedMessageService(FailedMessageDao failedMessageDao) {
        return new FailedMessageService(failedMessageDao);
    }
}
