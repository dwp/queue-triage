package uk.gov.dwp.queue.triage.web.component.health;

import org.junit.Test;
import uk.gov.dwp.queue.triage.web.component.SimpleBaseWebComponentTest;

public class PingComponentTest extends SimpleBaseWebComponentTest<PingStage> {

    @Test
    public void aRequestToPingReturnsPong() throws Exception {
        when().aRequestIsMadeToPing();
        then().theResponseIs("pong");
    }

}