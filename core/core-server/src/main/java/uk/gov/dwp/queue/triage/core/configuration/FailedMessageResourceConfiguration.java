package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoConfig;
import uk.gov.dwp.queue.triage.core.resource.create.CreateFailedMessageResource;
import uk.gov.dwp.queue.triage.core.resource.create.FailedMessageFactory;
import uk.gov.dwp.queue.triage.core.resource.label.LabelFailedMessageResource;
import uk.gov.dwp.queue.triage.core.resource.resend.ResendFailedMessageResource;
import uk.gov.dwp.queue.triage.core.resource.search.FailedMessageResponseFactory;
import uk.gov.dwp.queue.triage.core.resource.search.FailedMessageSearchResource;
import uk.gov.dwp.queue.triage.core.resource.status.FailedMessageStatusHistoryResource;
import uk.gov.dwp.queue.triage.core.resource.update.UpdateFailedMessageResource;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageResponseAdapter;
import uk.gov.dwp.queue.triage.core.service.FailedMessageLabelService;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;

import java.time.Clock;

@Configuration
@Import({
        MongoDaoConfig.class,
        CxfConfiguration.class,
})
public class FailedMessageResourceConfiguration {

    @Bean
    public CreateFailedMessageResource createFailedMessageResource(ResourceRegistry resourceRegistry,
                                                                   FailedMessageDao failedMessageDao) {
        return resourceRegistry.add(new CreateFailedMessageResource(new FailedMessageFactory(), failedMessageDao));
    }

    @Bean
    public FailedMessageSearchResource searchFailedMessageResource(ResourceRegistry resourceRegistry,
                                                                   FailedMessageDao failedMessageDao,
                                                                   FailedMessageSearchService failedMessageSearchService) {
        return resourceRegistry.add(new FailedMessageSearchResource(
                failedMessageDao,
                new FailedMessageResponseFactory(),
                failedMessageSearchService,
                new SearchFailedMessageResponseAdapter()
        ));
    }

    @Bean
    public ResendFailedMessageResource resendFailedMessageResource(ResourceRegistry resourceRegistry,
                                                                   FailedMessageService failedMessageService) {
        return resourceRegistry.add(new ResendFailedMessageResource(
                failedMessageService,
                Clock.systemUTC()));
    }

    @Bean
    public LabelFailedMessageResource labelFailedMessageResource(ResourceRegistry resourceRegistry,
                                                                 FailedMessageLabelService failedMessageLabelService) {
        return resourceRegistry.add(new LabelFailedMessageResource(
                failedMessageLabelService
        ));
    }

    @Bean
    public FailedMessageStatusHistoryResource failedMessageStatusHistoryResource(ResourceRegistry resourceRegistry,
                                                                                 FailedMessageDao failedMessageDao) {
        return resourceRegistry.add(new FailedMessageStatusHistoryResource(failedMessageDao));
    }

    @Bean
    public UpdateFailedMessageResource updateFailedMessageResource(ResourceRegistry resourceRegistry,
                                                                   FailedMessageService failedMessageService) {
        return resourceRegistry.add(new UpdateFailedMessageResource(failedMessageService));
    }
}
