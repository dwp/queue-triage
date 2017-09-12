package uk.gov.dwp.queue.triage.core;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import uk.gov.dwp.queue.triage.core.configuration.ChildContextConfiguration;
import uk.gov.dwp.queue.triage.core.configuration.CoreClientConfiguration;
import uk.gov.dwp.queue.triage.core.configuration.JGivenStageConfiguration;
import uk.gov.dwp.queue.triage.core.configuration.ParentContextConfiguration;
import uk.gov.dwp.queue.triage.core.stub.app.configuration.StubAppConfiguration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextHierarchy({
        @ContextConfiguration(classes = {ParentContextConfiguration.class}),
        @ContextConfiguration(classes = {
                ChildContextConfiguration.class,
                StubAppConfiguration.class,
                CoreClientConfiguration.class,
                JGivenStageConfiguration.class
        }),
})
@ActiveProfiles(value = "component-test")
public class BaseCoreComponentTest<STAGE> extends SimpleSpringRuleScenarioTest<STAGE> {

}
