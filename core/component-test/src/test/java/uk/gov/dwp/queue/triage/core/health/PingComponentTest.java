package uk.gov.dwp.queue.triage.core.health;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment= RANDOM_PORT)
public class PingComponentTest extends SimpleSpringRuleScenarioTest<PingStage> {

    @Test
    public void aRequestToPingReturnsPong() throws Exception {
        when().aRequestIsMadeToPing();
        then().theResponseIs("pong");
    }

}