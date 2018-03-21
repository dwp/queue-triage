package uk.gov.dwp.queue.triage.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.MessageConsumerApplicationInitializer;
import uk.gov.dwp.queue.triage.core.resend.spring.configuration.ResendFailedMessageApplicationInitializer;
import uk.gov.dwp.queue.triage.cxf.server.configuration.CxfBusConfiguration;
import uk.gov.dwp.queue.triage.cxf.server.configuration.CxfMetricsConfiguration;
import uk.gov.dwp.queue.triage.metrics.configuration.MetricRegistryConfiguration;
import uk.gov.dwp.queue.triage.metrics.servlet.configuration.ServletMetricsConfiguration;
import uk.gov.dwp.queue.triage.swagger.configuration.SwaggerConfiguration;

@SpringBootApplication
@Import({
        CxfBusConfiguration.class,
        CxfMetricsConfiguration.class,
        MetricRegistryConfiguration.class,
        ServletMetricsConfiguration.class,
        SwaggerConfiguration.class,
})
@EnableAutoConfiguration(
        exclude = {
                JmsAutoConfiguration.class,
                MongoAutoConfiguration.class,
                ActiveMQAutoConfiguration.class,
                SecurityAutoConfiguration.class,
        }
)
public class QueueTriageCoreApplication {

    public static void main(String[] args) {
        final SpringApplication springApplication = new SpringApplication(QueueTriageCoreApplication.class);
        springApplication.addInitializers(new MessageConsumerApplicationInitializer());
        springApplication.addInitializers(new ResendFailedMessageApplicationInitializer());
        springApplication.run(args);
    }
}
