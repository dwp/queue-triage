package uk.gov.dwp.queue.triage.web.component.health;

import org.junit.Test;
import uk.gov.dwp.queue.triage.web.component.BaseWebComponentTest;

public class PingComponentTest extends BaseWebComponentTest<PingStage> {

    @Test
    public void aRequestToPingReturnsPong() throws Exception {
        when().aRequestIsMadeToPing();
        then().theResponseIs("pong");
    }

}