package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.core.search.mongo.spring.MongoSearchConfiguration;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.core.service.processor.DisregardingFailedMessageProcessor;
import uk.gov.dwp.queue.triage.core.service.processor.ExistingFailedMessageProcessor;
import uk.gov.dwp.queue.triage.core.service.processor.PredicateOutcomeFailedMessageProcessor;
import uk.gov.dwp.queue.triage.core.service.processor.UniqueFailedMessageIdPredicate;
import uk.gov.dwp.queue.triage.core.service.processor.UniqueJmsMessageIdPredicate;

@Configuration
@Import({
        FailedMessageServiceConfiguration.class,
        MongoSearchConfiguration.class
})
public class FailedMessageProcessorConfiguration {

    @Bean
    public PredicateOutcomeFailedMessageProcessor failedMessageProcessor(FailedMessageSearchService failedMessageSearchService,
                                                                         FailedMessageService failedMessageService,
                                                                         FailedMessageDao failedMessageDao) {
        return new PredicateOutcomeFailedMessageProcessor(
                new UniqueJmsMessageIdPredicate(failedMessageSearchService),
                new PredicateOutcomeFailedMessageProcessor(
                        new UniqueFailedMessageIdPredicate(failedMessageDao),
                        failedMessageService::create,
                        new ExistingFailedMessageProcessor(failedMessageDao)
                ),
                new DisregardingFailedMessageProcessor(failedMessage -> "with JMSMessageId=" + failedMessage.getJmsMessageId() + " is not unique")
        );
    }
}
