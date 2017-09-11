package uk.gov.dwp.queue.triage.core.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "uk.gov.dwp.queue.triage.core.stage")
public class JGivenStageConfiguration {
}
