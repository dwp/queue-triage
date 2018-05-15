package uk.gov.dwp.queue.triage.core.health;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.dwp.queue.triage.core.SimpleCoreComponentTestBase;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment= RANDOM_PORT)
public class PingComponentTest extends SimpleCoreComponentTestBase<PingStage> {

    @Test
    public void aRequestToPingReturnsPong() throws Exception {
        when().aRequestIsMadeToPing();
        then().theResponseIs("pong");
    }

}