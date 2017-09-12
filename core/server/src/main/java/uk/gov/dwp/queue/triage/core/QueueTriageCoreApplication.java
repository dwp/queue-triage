package uk.gov.dwp.queue.triage.core;

import org.springframework.boot.builder.SpringApplicationBuilder;
import uk.gov.dwp.queue.triage.core.configuration.ChildContextConfiguration;
import uk.gov.dwp.queue.triage.core.configuration.ParentContextConfiguration;

public class QueueTriageCoreApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .sources(ParentContextConfiguration.class)
                .child(ChildContextConfiguration.class)
                .run(args);
    }
}
