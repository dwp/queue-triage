package uk.gov.dwp.queue.triage.core.search.mongo.spring;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.MongoStatusHistoryQueryBuilder;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoConfig;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.core.search.mongo.MongoFailedMessageSearchService;
import uk.gov.dwp.queue.triage.core.search.mongo.MongoSearchRequestAdapter;

@Configuration
@Import(MongoDaoConfig.class)
public class MongoSearchConfiguration {

    @Bean
    public FailedMessageSearchService failedMessageSearchService(MongoClient mongoClient,
                                                                 MongoDaoProperties mongoDaoProperties,
                                                                 FailedMessageConverter failedMessageConverter) {
        return new MongoFailedMessageSearchService(
                mongoClient.getDB(mongoDaoProperties.getDbName()).getCollection(mongoDaoProperties.getFailedMessage().getName()),
                new MongoSearchRequestAdapter(),
                failedMessageConverter,
                new MongoStatusHistoryQueryBuilder());
    }
}
