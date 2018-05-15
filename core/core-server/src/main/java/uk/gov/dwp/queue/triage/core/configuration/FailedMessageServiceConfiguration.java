package uk.gov.dwp.queue.triage.core.configuration;

import com.fasterxml.jackson.databind.InjectableValues;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.core.client.update.ContentUpdateRequest;
import uk.gov.dwp.queue.triage.core.client.update.DestinationUpdateRequest;
import uk.gov.dwp.queue.triage.core.client.update.PropertiesUpdateRequest;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.ContentUpdateRequestAdapter;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.DestinationUpdateRequestAdapter;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.LoggingUpdateRequestAdapter;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.PropertiesUpdateRequestAdapter;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.StatusUpdateRequestAdapter;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.UpdateRequestAdapterRegistry;
import uk.gov.dwp.queue.triage.core.service.FailedMessageBuilderFactory;
import uk.gov.dwp.queue.triage.core.service.FailedMessageLabelService;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

@Configuration
public class FailedMessageServiceConfiguration {

    @Bean
    public FailedMessageService failedMessageService(FailedMessageDao failedMessageDao,
                                                     InjectableValues.Std jacksonInjectableValues) {
        FailedMessageService failedMessageService = new FailedMessageService(
                failedMessageDao,
                new FailedMessageBuilderFactory(new UpdateRequestAdapterRegistry(new LoggingUpdateRequestAdapter())
                        .addAdapter(ContentUpdateRequest.class, new ContentUpdateRequestAdapter())
                        .addAdapter(DestinationUpdateRequest.class, new DestinationUpdateRequestAdapter())
                        .addAdapter(PropertiesUpdateRequest.class, new PropertiesUpdateRequestAdapter())
                        .addAdapter(StatusUpdateRequest.class, new StatusUpdateRequestAdapter()),
                        failedMessageDao));
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
