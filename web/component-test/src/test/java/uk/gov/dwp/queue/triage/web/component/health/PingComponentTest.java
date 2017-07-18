package uk.gov.dwp.queue.triage.web.component.health;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.dwp.queue.triage.web.server.QueueTriageWebApplication;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {
                QueueTriageWebApplication.class,
                PingTestContext.class
        })
public class PingComponentTest extends SimpleSpringRuleScenarioTest<PingStage> {

    @Test
    public void aRequestToPingReturnsPong() throws Exception {
        when().aRequestIsMadeToPing();
        then().theResponseIs("pong");
    }

}