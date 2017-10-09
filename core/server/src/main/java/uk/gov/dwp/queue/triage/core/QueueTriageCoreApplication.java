package uk.gov.dwp.queue.triage.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.cxf.server.configuration.CxfBusConfiguration;
import uk.gov.dwp.queue.triage.cxf.server.configuration.CxfMetricsConfiguration;
import uk.gov.dwp.queue.triage.metrics.configuration.MetricRegistryConfiguration;
import uk.gov.dwp.queue.triage.metrics.servlet.configuration.ServletMetricsConfiguration;
import uk.gov.dwp.queue.triage.swagger.configuration.SwaggerConfiguration;

@SpringBootApplication
@Import({
        CxfBusConfiguration.class,
        MetricRegistryConfiguration.class,
        ServletMetricsConfiguration.class,
        SwaggerConfiguration.class,
})
@EnableAutoConfiguration(
        exclude = {
                MongoAutoConfiguration.class,
                ActiveMQAutoConfiguration.class,
                SecurityAutoConfiguration.class,
        }
)
public class QueueTriageCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueTriageCoreApplication.class, args);
    }
}
