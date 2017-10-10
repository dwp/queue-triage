package uk.gov.dwp.queue.triage.web.component;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.dwp.queue.triage.web.server.QueueTriageWebApplication;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {
                QueueTriageWebApplication.class,
        }
)
@ActiveProfiles(value = "component-test")
@ComponentScan
public class SimpleBaseWebComponentTest<STAGE> extends SimpleSpringRuleScenarioTest<STAGE> {

}
