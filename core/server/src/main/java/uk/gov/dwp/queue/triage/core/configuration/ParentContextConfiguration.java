package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.migration.mongo.demo.cxf.client.CxfConfiguration;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoConfig;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerProperties;
import uk.gov.dwp.queue.triage.core.search.mongo.spring.MongoSearchConfiguration;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;
import uk.gov.dwp.queue.triage.swagger.configuration.SwaggerConfiguration;

@Configuration
@Import({
        CxfConfiguration.class,
        JacksonConfiguration.class,
        SwaggerConfiguration.class,
//        MongoDaoProperties.class,
//        JmsListenerProperties.class,
        MongoDaoConfig.class,
        MongoSearchConfiguration.class,
        FailedMessageServiceConfiguration.class
})
public class ParentContextConfiguration {
}
