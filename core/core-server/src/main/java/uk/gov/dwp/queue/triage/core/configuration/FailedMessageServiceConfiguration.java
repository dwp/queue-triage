package uk.gov.dwp.queue.triage.core.configuration;

import com.fasterxml.jackson.databind.InjectableValues;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.service.FailedMessageLabelService;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

@Configuration
public class FailedMessageServiceConfiguration {

    @Bean
    public FailedMessageService failedMessageService(FailedMessageDao failedMessageDao,
                                                     InjectableValues.Std jacksonInjectableValues) {
        FailedMessageService failedMessageService = new FailedMessageService(failedMessageDao);
        jacksonInjectableValues.addValue(FailedMessageService.class, failedMessageService);
        return failedMessageService;
    }

    @Bean
    public FailedMessageLabelService failedMessageLabelService(FailedMessageDao failedMessageDao,
                                                               InjectableValues.Std jacksonInjectableValues) {
        FailedMessageLabelService failedMessageLabelService = new FailedMessageLabelService(failedMessageDao);
        jacksonInjectableValues.addValue(FailedMessageLabelService.class, failedMessageLabelService);
        return failedMessageLabelService;
    }
}
