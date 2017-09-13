package uk.gov.dwp.queue.triage.core;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(value = "component-test")
public class BaseCoreComponentTest<STAGE> extends SimpleSpringRuleScenarioTest<STAGE> {

}
