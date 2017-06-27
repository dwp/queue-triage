package uk.gov.dwp.queue.triage.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.migration.mongo.demo.cxf.configuration.CxfBusConfiguration;

@SpringBootApplication
@Import({
        CxfBusConfiguration.class
})
public class QueueTriageCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueTriageCoreApplication.class, args);
    }
}
