package uk.gov.dwp.queue.triage.core.health;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.stage.health.PingStage;

public class PingComponentTest extends BaseCoreComponentTest<PingStage> {

    @Test
    public void aRequestToPingReturnsPong() throws Exception {
        when().aRequestIsMadeToPing();
        then().theResponseIs("pong");
    }

}