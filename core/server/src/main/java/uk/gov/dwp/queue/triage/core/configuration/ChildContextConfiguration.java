package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import uk.gov.dwp.migration.mongo.demo.cxf.configuration.CxfBusConfiguration;
import uk.gov.dwp.queue.triage.core.classification.server.configuration.MessageClassificationConfiguration;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerConfig;
import uk.gov.dwp.queue.triage.core.resend.spring.configuration.ResendFailedMessageConfiguration;

/**
 * As per the <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-spring-application.html#boot-features-fluent-builder-api">
 * Spring documentation</a>, the Child Application Context MUST contain the web components
 */
@Component
@Import({
        MessageClassificationConfiguration.class,
        FailedMessageResourceConfiguration.class,
        RemoveFailedMessageConfiguration.class,
        JmsListenerConfig.class,
        ResendFailedMessageConfiguration.class,
        HealthCheckConfiguration.class,
        CxfBusConfiguration.class,
        ServerProperties.class, // would ServerPropertiesAutoConfiguration.class be a better choice?
        EmbeddedServletContainerAutoConfiguration.class

})
public class ChildContextConfiguration {
}
